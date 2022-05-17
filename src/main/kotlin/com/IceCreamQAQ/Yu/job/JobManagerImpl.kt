package com.IceCreamQAQ.Yu.job

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Cron
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.events.JobRunExceptionEvent
import com.IceCreamQAQ.Yu.fullName
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import com.IceCreamQAQ.Yu.util.DateUtil
import com.IceCreamQAQ.Yu.util.uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.newFixedThreadPoolContext
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.jvm.kotlinFunction

@AutoBind
interface JobManager {

    fun registerTimer(runnable: Runnable, firstTime: Long): String =
        registerTimer(runnable, firstTime, null)

    fun registerTimer(runnable: Runnable, firstTime: Long, nextTime: Long?): String =
        registerTimer(firstTime, nextTime) {
            runnable.run()
        }

    @JvmSynthetic
    fun registerTimer(firstTime: Long, nextTime: Long? = null, function: suspend () -> Unit): String

    fun registerTimer(runnable: Runnable, atTime: String): String =
        registerTimer(runnable, atTime, false)

    fun registerTimer(runnable: Runnable, atTime: String, always: Boolean): String =
        registerTimer(atTime, always) {
            runnable.run()
        }

    @JvmSynthetic
    fun registerTimer(atTime: String, always: Boolean = true, function: suspend () -> Unit): String

    fun deleteTimer(id: String): Boolean
}

class JobManagerImpl : ApplicationService, Loader, JobManager {

    companion object {
        private val log = LoggerFactory.getLogger(JobManager::class.java)
    }

    private val scope = object : CoroutineScope {
        override val coroutineContext = newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors(), "Job")

    }

    private var jobs: MutableMap<String, JobInfo> = HashMap()

    @Inject
    private lateinit var dateUtil: DateUtil

    @Inject
    private lateinit var context: YuContext

    @Inject
    private lateinit var eventBus: EventBus

    val errorCallback: (JobInfo, Throwable) -> Unit = { j, e -> eventBus.post(JobRunExceptionEvent(j, e)) }

    private fun getFirstTime(time: String): Long {
        val d = !time.startsWith(":")
        val date = Date()

        return if (d) {
            val ds = dateUtil.formatDate(date)
            val dd = dateUtil.parseDateTime("$ds $time:00")
            var tt = dd.time - date.time
            if (tt < 0) tt += 24 * 60 * 60 * 1000
            tt
        } else {
            val ds = dateUtil.formatDateTime(date)
            val dd = dateUtil.parseDateTime("${ds.subSequence(0, 13)}$time:00")
            var tt = dd.time - date.time
            if (tt < 0) tt += 60 * 60 * 1000
            tt
        }
    }

    private fun getTime(timeStr: String): Pair<Long, Long> =
        if (timeStr.contains(":"))
            if (timeStr.startsWith("At::"))
                timeStr.split("::").let {
                    if (it[1] == "h") getFirstTime(":${it[2]}") to 60 * 60 * 1000
                    else getFirstTime(it[2]) to 24 * 60 * 60 * 1000
                }
            else
                timeStr.split(":").let {
                    getFirstTime(timeStr) to if (it[1] == "") 60 * 60 * 1000
                    else 24 * 60 * 60 * 1000
                }
        else {
            var time = 0L
            var cTime = ""
            for (c in timeStr) {
                if (Character.isDigit(c)) cTime += c
                else {
                    val cc = cTime.toLong()
                    time += when (c) {
                        'y' -> cc * 1000 * 60 * 60 * 24 * 365
                        'M' -> cc * 1000 * 60 * 60 * 24 * 30
                        'd' -> cc * 1000 * 60 * 60 * 24
                        'h' -> cc * 1000 * 60 * 60
                        'm' -> cc * 1000 * 60
                        's' -> cc * 1000
                        'S' -> cc
                        else -> 0L
                    }
                    cTime = ""
                }
            }
            time to time
        }

    override fun load(items: Map<String, LoadItem>) {
        for (item in items.values) {
            if (!item.type.isBean()) continue
            log.debug("Register JobCenter: ${item.type.name}.")
            val instance = context[item.type] ?: continue
            val methods = item.type.methods
            for (method in methods) {
                val cron = method.getAnnotation(Cron::class.java) ?: continue
                val fullName = method.fullName
                log.trace("Register Job: $fullName.")
                val job =
                    getTime(cron.value).let {
                        JobInfo(
                            fullName,
                            it.first,
                            it.second,
                            cron.async,
                            cron.runWithStart,
                            makeInvoker(instance, method),
                            scope,
                            errorCallback
                        )
                    }
                jobs[uuid()] = (job)
                log.trace("Register Job: $fullName Success!")
            }
            log.info("Register JobCenter: ${item.type.name} Success!")
        }
    }

    private fun makeInvoker(instance: Any, method: Method) =
        method.kotlinFunction?.let {
            if (it.isSuspend) KSuspendFunReflectCronInvoker(instance, it)
            else KFunReflectCronInvoker(instance, it)
        } ?: ReflectCronInvoker(instance, method)

    override fun width() = 10

    override fun init() {}

    override fun start() {
        for (job in jobs.values) {
            job.start()
        }
    }

    override fun stop() {
        for (job in jobs.values) {
            job.close()
        }
    }

    private fun getInvokerInfo() =
        Thread.currentThread().stackTrace.let {
            for (i in 1 until it.size) {
                val item = it[i]
                if (!item.className.startsWith("com.IceCreamQAQ.Yu.job."))
                    return@let "${item.className}.${item.methodName}(${item.fileName}:${item.lineNumber})"
            }
            return@let "无法检索到运行位置！"
        }


    override fun registerTimer(firstTime: Long, nextTime: Long?, function: suspend () -> Unit): String {
        val name = getInvokerInfo()
        val id = uuid()
        jobs[id] = JobInfo(
            name,
            firstTime,
            nextTime ?: -1,
            async = false,
            runWithStart = false,
            invoker = DslCronInvoker(function),
            scope = scope,
            errorCallback
        ) {
            jobs.remove(id)
        }.apply { start() }
        return id
    }

    override fun registerTimer(atTime: String, always: Boolean, function: suspend () -> Unit): String {
        val name = getInvokerInfo()
        val id = uuid()
        jobs[id] = getTime(if (atTime.length == 2) ":$atTime" else atTime).let {
            JobInfo(
                name,
                it.first,
                if (always) it.second else -1,
                async = false,
                runWithStart = false,
                invoker = DslCronInvoker(function),
                scope = scope,
                errorCallback
            ) {
                jobs.remove(id)
            }
        }.apply { start() }

        return id
    }

    override fun deleteTimer(id: String) = jobs.remove(id)?.apply { close() } != null


}

class JobScope : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}
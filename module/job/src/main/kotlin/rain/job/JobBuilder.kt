package rain.job

import rain.function.DateUtil
import rain.function.currentTimeMillis
import rain.function.toTime
import java.time.ZoneId

class JobBuilder(
    val name: String? = null,
) {

    internal var timeFun: NextTime? = null
    internal var async: Boolean = false
    internal var runWithStart: Boolean = false
    internal var invoker: CronInvoker? = null


    internal var firstTime: Long? = null
    internal var nextTime: Long? = null

    private fun of(body: () -> Unit): JobBuilder {
        body()
        return this
    }

    fun every(time: Long) = of {
        timeFun = NextTime { _, _ -> time }
    }

    fun every(time: String) = every(time.toTime())

    fun first(time: Long) = of { firstTime = time }
    fun next(time: Long) = of { nextTime = time }

    fun async() = of { async = true }
    fun runWithStart() = of { runWithStart = true }

    fun task(runnable: Runnable) = of { invoker = CronInvoker.runnable(runnable) }
    fun task(body: suspend () -> Unit) = of { invoker = CronInvoker.dsl(body) }

    private var at: Long? = null
    fun at(time: String) = of {
        val tf = time.split(":")
        fun e(): Nothing = error("时间格式解析失败！at 必须接受为 mm:ss 或是 HH:mm:ss 格式！")

        val ts = when (tf.size) {
            1 -> e()
            2 -> {
                at = 60 * 60 * 1000
                "${DateUtil.formatDateTime().substring(0, 14)}${tf[0]}:${tf[1]}"
            }

            3 -> {
                at = 24 * 60 * 60 * 1000
                "${DateUtil.formatDate()} ${tf[0]}:${tf[1]}:${tf[2]}"
            }

            else -> e()
        }
        var tl = DateUtil.parseDateTime(ts).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val cl = currentTimeMillis
        if (tl < cl) tl += at!!
        firstTime = tl - cl
    }

    fun alaways() = of {
        if (at == null) error("请正确调用 at 函数提供执行时间！")
        nextTime = at
    }

    fun cron(time: String) {
        TODO()
    }

    private fun getInvokerInfo() =
        Thread.currentThread().stackTrace.first { !it.className.startsWith("rain.job.") }
            ?.run { "$className.$methodName($fileName:$lineNumber)" }

    fun build(): JobRuntime {
        val time =
            timeFun
                ?: firstTime?.let {
                    if (nextTime == null) NextTime { i, _ -> if (i != -1L) -1 else it }
                    else if (it == nextTime) NextTime { _, _ -> it }
                    else NextTime { i, _ -> if (i != -1L) nextTime!! else it }
                } ?: error("任务没有正确的执行时间")

        return JobRuntime(
            name ?: getInvokerInfo() ?: "未命名任务",
            async,
            time,
            invoker ?: error("任务没有正确的执行体！")
        )
    }

}
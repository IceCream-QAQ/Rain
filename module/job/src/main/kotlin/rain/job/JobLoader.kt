package rain.job

import rain.api.di.DiContext
import rain.api.di.DiContext.Companion.get
import rain.api.loader.LoadItem
import rain.api.loader.Loader
import rain.function.fullName
import rain.function.isBean
import rain.function.slf4j
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction

class JobLoader(
    val context: DiContext,
    val jobManager: JobManager
) : Loader {

    companion object {
        val log = slf4j()
    }

    override fun load(items: Collection<LoadItem>) {
        log.info("[时钟任务] 开始加载时钟任务。")
        for (item in items) {
            if (!item.clazz.isBean) continue
            log.debug("[时钟任务] 注册时钟任务类: ${item.clazz.name}.")
            val instance = context[item.clazz] ?: continue
            val methods = item.clazz.methods
            for (method in methods) {
                val cron = method.getAnnotation(Cron::class.java) ?: continue
                val fullName = method.fullName
                log.trace("[时钟任务] 注册时钟任务: $fullName.")
                val f = cron.value
                val jb = JobBuilder(fullName)
                if (f.contains(" ")) jb.cron(f)
                else if (f.contains(":")) jb.at(f).alaways()
                else jb.every(f)
                if (cron.async) jb.async()
                if (cron.runWithStart) jb.runWithStart()
                jb.task(makeInvoker(instance, method))

                jobManager.registerJob(jb.build())
                log.trace("[时钟任务] 注册时钟任务: $fullName 成功!")
            }
            log.info("[时钟任务] 注册时钟任务类: ${item.clazz.name} 成功!")
        }
        log.info("[时钟任务] 时钟任务启动完成。")
    }

    private fun makeInvoker(instance: Any, method: Method) =
        method.kotlinFunction?.let {
            if (it.isSuspend) CronInvoker.suspendFun(instance, it)
            else CronInvoker.kFun(instance, it)
        } ?: CronInvoker.reflect(instance, method)
}
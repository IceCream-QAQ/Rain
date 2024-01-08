package rain.job

import rain.api.event.EventBus
import rain.api.loader.ApplicationService
import rain.function.cccPool
import rain.function.uuid

class JobCenter(
    eventBus: EventBus?
) : JobManager, ApplicationService {

    val errorCallback: (JobRuntime, Throwable) -> Unit =
        eventBus?.let { { j, e -> it.post(JobRunExceptionEvent(j, e)) } }
            ?: { _, _ -> }

    private val scope = cccPool("Job")
    private var jobs: MutableMap<String, JobRuntime> = HashMap()

    private fun getInvokerInfo() =
        Thread.currentThread().stackTrace.first { !it.className.startsWith("rain.job.") }
            ?.run { "$className.$methodName($fileName:$lineNumber)" }

    override fun newJob(name: String?): JobBuilder =
        JobBuilder(this, name ?: getInvokerInfo() ?: "Unnamed Job")

    override fun registerJob(job: JobBuilder): String {
        val id = uuid()
        val jr = JobRuntime(
            job.name,
            job.firstTime ?: error("请正确设置任务响应时间！"),
            job.nextTime ?: 0,
            job.async,
            job.runWithStart,
            job.invoker ?: error("请正确设置任务执行体！"),
            scope,
            errorCallback
        ){
            jobs.remove(id)
        }
        jobs[id] = jr
        jr.start()
        return id
    }

    override fun deleteTimer(id: String): Boolean = jobs.remove(id)?.apply { close() } != null


    override fun start() {
        jobs.values.forEach { it.start() }
    }

    override fun stop() {
        jobs.values.forEach { it.close() }
    }
}
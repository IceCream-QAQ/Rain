package rain.job

import rain.api.event.EventBus
import rain.api.loader.ApplicationService
import rain.function.coreNumCoroutineScope
import rain.function.uuid

class JobCenter(
    eventBus: EventBus?
) : JobManager, ApplicationService {

    val errorCallback: (JobRuntime, Throwable) -> Unit =
        eventBus?.let { { j, e -> it.post(JobRunExceptionEvent(j, e)) } }
            ?: { _, _ -> }

    private val scope = coreNumCoroutineScope("Job")
    private var jobs: MutableMap<String, JobRuntime> = HashMap()

    override fun registerJob(job: JobRuntime): String {
        val id = uuid()
        job.registerScope(scope)
        job.registerErrorCallback { errorCallback(job, it) }
        job.registerEndCallback { jobs.remove(id) }

        jobs[id] = job
        job.start()
        return id
    }

    override fun deleteTimer(id: String): Boolean = jobs.remove(id)?.apply { close() } != null


    override fun start() {
//        jobs.values.forEach { it.start() }
    }

    override fun stop() {
        jobs.values.forEach { it.close() }
    }
}
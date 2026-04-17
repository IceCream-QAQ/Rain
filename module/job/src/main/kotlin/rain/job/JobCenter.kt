package rain.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import rain.api.event.EventBus
import rain.api.loader.ApplicationService
import rain.function.coreNumThreadPool
import rain.function.uuid
import java.util.concurrent.ConcurrentHashMap

class JobCenter(
    eventBus: EventBus?
) : JobManager, ApplicationService {

    val errorCallback: (JobRuntime, Throwable) -> Unit =
        eventBus?.let { { j, e -> it.post(JobRunExceptionEvent(j, e)) } }
            ?: { _, _ -> }

    private val threadPool = coreNumThreadPool("Job")
    private val scope = CoroutineScope(SupervisorJob() + threadPool)

    private var jobs: MutableMap<String, JobRuntime> = ConcurrentHashMap()

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
        scope.cancel()
        threadPool.close()
    }
}
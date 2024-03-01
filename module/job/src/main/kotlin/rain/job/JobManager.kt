package rain.job

import rain.api.annotation.AutoBind

@AutoBind
interface JobManager {

    fun registerJob(job: JobRuntime): String
    fun deleteTimer(id: String): Boolean

}
package rain.job

interface JobManager {

    fun registerJob(job: JobRuntime): String
    fun deleteTimer(id: String): Boolean

}
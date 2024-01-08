package rain.job

interface JobManager {

    fun newJob(): JobBuilder = newJob(null)
    fun newJob(name: String?): JobBuilder

    fun registerJob(job: JobBuilder): String
    fun deleteTimer(id: String): Boolean

}
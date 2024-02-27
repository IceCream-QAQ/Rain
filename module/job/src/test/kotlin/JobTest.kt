import rain.job.JobBuilder
import rain.job.JobCenter


fun main(){
    val jc = JobCenter(null)

    JobBuilder("aaa")
        .at("32:00")
        .task {
            println("on 5s")
        }
        .build()
        .let { jc.registerJob(it) }

    Thread.sleep(100 * 1000)
}
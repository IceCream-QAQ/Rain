import rain.job.JobBuilder
import rain.job.JobCenter


fun main(){
    val jc = JobCenter(null)

    JobBuilder("aaa")
        .every("5s")
        .task {
            println("on 5s")
        }
        .build()
        .let { jc.registerJob(it) }

    Thread.sleep(100 * 1000)
}
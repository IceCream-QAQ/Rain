import rain.job.JobCenter


fun main(){
    val jc = JobCenter(null)

    jc.newJob()
        .every("5s")
        .task {
            println("on 5s")
        }
        .register()

    Thread.sleep(100 * 1000)
}
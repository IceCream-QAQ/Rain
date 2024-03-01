package application

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import rain.job.Cron
import rain.job.CronTab
import rain.test.RainTest

@RainTest
@CronTab
class ApplicationJobTest {

    var flag = false

    @Cron("1s")
    fun timer() {
        flag = true
    }

    @Test
    fun application() {
        runBlocking { delay(2000) }
        if (!flag) error("定时任务没有被正确执行！")
    }

}
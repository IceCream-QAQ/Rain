package nonApplication

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import rain.function.DateUtil
import rain.function.currentTimeMillis
import rain.job.JobBuilder
import rain.job.JobCenter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NonApplicationJobTest {
    val center = JobCenter(null)

    @Test
    fun testAtHour() {
        var result = false
        LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis + 2000), ZoneId.systemDefault())
            .let { DateUtil.formatDateTime(it) }
            .substring(14)
            .let {
                JobBuilder("TestAtHour")
                    .at(it)
                    .task {
                        result = true
                    }
                    .build()
            }
            .let { center.registerJob(it) }

        runBlocking { delay(2200) }

        if (!result) error("TestAtHour failed")
        println("TestAtHourSuccess")
    }

    @Test
    fun testAtDay() {
        var result = false
        LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis + 1000), ZoneId.systemDefault())
            .let { DateUtil.formatDateTime(it) }
            .substring(11)
            .let {
                JobBuilder("TestAtDay")
                    .at(it)
                    .task {
                        result = true
                    }
                    .build()
            }
            .let { center.registerJob(it) }

        runBlocking { delay(1100) }

        if (!result) error("TestAtDay failed")
        println("TestAtDaySuccess")
    }

}

fun main() {
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
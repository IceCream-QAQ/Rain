package rain.job

import rain.function.DateUtil
import rain.function.toTime
import java.time.ZoneOffset

class JobBuilder(
    val jobManager: JobManager,
    val name: String,
) {

    internal var firstTime: Long? = null
    internal var nextTime: Long? = null
    internal var async: Boolean = false
    internal var runWithStart: Boolean = false
    internal var invoker: CronInvoker? = null

    private fun of(body: () -> Unit): JobBuilder {
        body()
        return this
    }

    fun every(time: Long) = of {
        firstTime = time
        nextTime = time
    }

    fun every(time: String) = every(time.toTime())

    fun first(time: Long) = of { firstTime = time }
    fun next(time: Long) = of { nextTime = time }

    fun async() = of { async = true }
    fun runWithStart() = of { runWithStart = true }

    fun task(runnable: Runnable) = of { invoker = CronInvoker.runnable(runnable) }
    fun task(body: suspend () -> Unit) = of { invoker = CronInvoker.dsl(body) }

    private var at: Int? = null
    fun at(time: String) = of {
        val tf = time.split(":")
        fun e(): Nothing = error("时间格式解析失败！at 必须接受为 HH:mm 或是 HH:mm:ss 格式！")

        val t = when (tf.size) {
            1 -> e()
            2 -> {
                at = 1
                DateUtil.parseDate("${DateUtil.formatDate()} ${tf[0]}:${tf[1]}:00")
                    .toInstant(ZoneOffset.UTC)
                    .let {

                    }
                TODO()
            }

            3 -> {
                at = 2
                TODO()
            }

            else -> e()
        }
    }

    fun alaways() = of {
        if (at == null) error("请正确调用 at 函数提供执行时间！")
        when (at) {
            0 -> nextTime = 60 * 1000
            1 -> nextTime = 60 * 60 * 1000
            2 -> nextTime = 24 * 60 * 60 * 1000
        }
    }

    fun cron(time: String) {
        TODO()
    }


    fun register() {
        jobManager.registerJob(this)
    }

}
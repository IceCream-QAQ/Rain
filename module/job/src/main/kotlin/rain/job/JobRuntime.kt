package rain.job

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.Closeable

class JobRuntime(
    val name: String,
    private val firstTime: Long,
    private val nextTime: Long,
    private val async: Boolean,
    private val runWithStart: Boolean,
    private val invoker: CronInvoker,
    private val scope: CoroutineScope,
    private val errorCallback: (JobRuntime, Throwable) -> Unit,
    private val endCallback: () -> Unit = {}
) : Closeable {


    companion object {
        private val log = LoggerFactory.getLogger(JobRuntime::class.java)
    }

    private lateinit var job: Job

    suspend operator fun invoke() {
        kotlin.runCatching {
            if (async) scope.launch { invoker() }
            else invoker()
        }.getOrElse {
            log.error("任务: \"$name\" 运行时异常!", it)
            errorCallback(this, it)
        }
    }

    internal fun start() {
        job = scope.launch {
            if (runWithStart) invoke()
            var next = firstTime
            while (next > 0) {
                delay(next)
                val start = System.currentTimeMillis()
                invoke()
                val end = System.currentTimeMillis()
                val cost = (end - start) % nextTime
                next = nextTime - cost
            }
            endCallback()
        }
    }

    override fun close() {
        job.cancel()
    }
}
package rain.job

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.Closeable

class JobRuntime(
    val name: String,
    private val async: Boolean,
    private val nextTime: NextTime,
    private val invoker: CronInvoker,
) : Closeable {


    private lateinit var scope: CoroutineScope
    private lateinit var errorCallback: (Throwable) -> Unit
    private lateinit var endCallback: () -> Unit

    internal fun registerScope(scope: CoroutineScope) {
        this.scope = scope
    }

    internal fun registerErrorCallback(errorCallback: (Throwable) -> Unit) {
        this.errorCallback = errorCallback
    }

    internal fun registerEndCallback(callback: () -> Unit) {
        endCallback = callback
    }

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
            errorCallback(it)
        }
    }

    internal fun start() {
        job = scope.launch {
            var invokeTime = -1L
            var endTime = -1L
            while (true) {
                val nextTime = nextTime(invokeTime, endTime)
                if (nextTime < 0) break

                delay(nextTime)
                invokeTime = System.currentTimeMillis()
                invoke()
                endTime = System.currentTimeMillis()
            }
            endCallback()
        }
    }

    override fun close() {
        job.cancel()
    }
}
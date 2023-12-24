package rain.function

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newFixedThreadPoolContext

inline fun getCaller(): Class<*> {
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[1]
    return Class.forName(caller.className)
}

fun ccPool(name: String) =
    newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors(), name)

fun cc2pool(name: String) =
    newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() * 2, name)

fun cccPool(name: String) = object : CoroutineScope {
    override val coroutineContext = ccPool(name)
}
fun ccc2pool(name: String) = object : CoroutineScope {
    override val coroutineContext = cc2pool(name)
}
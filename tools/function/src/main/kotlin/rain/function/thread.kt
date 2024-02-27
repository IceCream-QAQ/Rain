package rain.function

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newFixedThreadPoolContext

inline fun getCallerName(): String =
    Thread.currentThread().stackTrace[2].className

inline fun getCallPackage(): String =
    getCallerName().let { call -> call.lastIndexOf(".").let { if (it != -1) call.substring(0, it) else "" } }


inline fun getCaller(): Class<*> =
    Class.forName(Thread.currentThread().stackTrace[1].className)


fun coreNumThreadPool(name: String) =
    newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors(), name)

fun coreNum2ThreadPool(name: String) =
    newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() * 2, name)

fun coreNumCoroutineScope(name: String) = object : CoroutineScope {
    override val coroutineContext = coreNumThreadPool(name)
}

fun coreNum2CoroutineScope(name: String) = object : CoroutineScope {
    override val coroutineContext = coreNum2ThreadPool(name)
}
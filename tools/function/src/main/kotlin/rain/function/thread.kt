package rain.function

inline fun getCaller(): Class<*>{
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[1]
    return Class.forName(caller.className)
}
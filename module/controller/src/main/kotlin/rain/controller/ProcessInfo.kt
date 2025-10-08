package rain.controller

data class ProcessInfo<T : ActionContext>(
    val providerAnnotation: Annotation,
    val functionAnnotation: Annotation,
    val filter: ProcessFilter?,
    val priority: Int,
    val except: Array<String>,
    val only: Array<String>,
    val invoker: ProcessInvoker<T>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessInfo<*>

        if (priority != other.priority) return false
        if (!except.contentEquals(other.except)) return false
        if (!only.contentEquals(other.only)) return false
        if (invoker != other.invoker) return false

        return true
    }

    override fun hashCode(): Int {
        var result = priority
        result = 31 * result + except.contentHashCode()
        result = 31 * result + only.contentHashCode()
        result = 31 * result + invoker.hashCode()
        return result
    }
}
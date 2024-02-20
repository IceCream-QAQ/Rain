package rain.job

fun interface NextTime {
    operator fun invoke(invokeTime: Long, endTime: Long): Long
}
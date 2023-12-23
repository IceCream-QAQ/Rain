package rain.function

fun String.subStringByLast(range: IntRange): String {
    val first = substring(0, length - range.last)
    val end = substring(length - range.first)
    return first + end
}

fun String.subStringByLast(length: Int) = substring(0,this.length - length)
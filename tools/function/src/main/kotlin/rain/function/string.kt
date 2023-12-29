package rain.function

fun String.subStringByLast(range: IntRange): String {
    val first = substring(0, length - range.last)
    val end = substring(length - range.first)
    return first + end
}

fun String.subStringByLast(length: Int) = substring(0,this.length - length)

fun String.toUpperCaseFirstOne(): String {
    return if (Character.isUpperCase(this[0])) this
    else (StringBuilder()).append(Character.toUpperCase(this[0])).append(this.substring(1)).toString();
}

fun String.toLowerCaseFirstOne(): String {
    return if (Character.isLowerCase(this[0])) this
    else (StringBuilder()).append(Character.toLowerCase(this[0])).append(this.substring(1)).toString();
}

fun String.toTime(): Long {
    var time = 0L
    var cTime = ""
    for (c in this) {
        if (Character.isDigit(c)) cTime += c
        else {
            val cc = cTime.toLong()
            time += when (c) {
                'y' -> cc * 1000 * 60 * 60 * 24 * 365
                'M' -> cc * 1000 * 60 * 60 * 24 * 30
                'd' -> cc * 1000 * 60 * 60 * 24
                'h' -> cc * 1000 * 60 * 60
                'm' -> cc * 1000 * 60
                's' -> cc * 1000
                'S' -> cc
                else -> 0L
            }
            cTime = ""
        }
    }
    return time
}
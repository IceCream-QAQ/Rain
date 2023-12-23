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
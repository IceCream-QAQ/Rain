package rain.function.dataNode

import rain.function.type.RelType

class StringNode(var content: String) : BaseNode() {
    override fun asBoolean(): Boolean = content.toBoolean()

    override fun asByte(): Byte = content.toByte()

    override fun asShort(): Short = content.toShort()

    override fun asInt(): Int = content.toInt()

    override fun asLong(): Long = content.toLong()

    override fun asFloat(): Float = content.toFloat()

    override fun asDouble(): Double = content.toDouble()

    override fun asChar(): Char = content[0]

    override fun asString(): String = content

    override fun <T> asEnum(enumClass: RelType<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> castTo(type: RelType<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> asArray(type: RelType<T>): List<T> =
        listOf(asObject(type))

    override fun <T> asMap(type: RelType<T>): Map<String, T> = error("无法将一个 StringNode 转换为 Map！")

    override fun toString(): String = "StringNode: $content"
}
package com.IceCreamQAQ.Yu.util.dataNode

import com.IceCreamQAQ.Yu.util.type.RelType
import java.util.LinkedList

open class ArrayNode() : BaseNode(), MutableIterable<DataNode> {

    protected open val list = LinkedList<DataNode>()

    constructor(vararg data: DataNode) : this() {
        data.forEach { add(it) }
    }

    open fun addFirst(data: DataNode) {
        list.addFirst(data)
    }

    open fun add(data: DataNode) {
        list.add(data)
    }

    open fun addAll(arrayNode: ArrayNode) {
        list.addAll(arrayNode.list)
    }

    override fun asBoolean(): Boolean = error("无法将一个 Array 数据节点转化为 Boolean！")

    override fun asByte(): Byte = error("无法将一个 Array 数据节点转化为 Byte！")

    override fun asShort(): Short = error("无法将一个 Array 数据节点转化为 Short！")

    override fun asInt(): Int = error("无法将一个 Array 数据节点转化为 Int！")

    override fun asLong(): Long = error("无法将一个 Array 数据节点转化为 Long！")

    override fun asFloat(): Float = error("无法将一个 Array 数据节点转化为 Float！")

    override fun asDouble(): Double = error("无法将一个 Array 数据节点转化为 Double！")

    override fun asChar(): Char = error("无法将一个 Array 数据节点转化为 Char！")

    override fun asString(): String = error("无法将一个 Array 数据节点转化为 String！")

    override fun <T> castTo(type: RelType<T>): T {
        return list.first().asObject(type)
    }

    override fun <T> asEnum(enumClass: RelType<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> asArray(type: RelType<T>): List<T> {
        return list.map { it.asObject(type) }
    }

    override fun iterator() = list.iterator()
}
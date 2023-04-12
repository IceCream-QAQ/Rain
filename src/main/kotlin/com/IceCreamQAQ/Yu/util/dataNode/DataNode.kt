package com.IceCreamQAQ.Yu.util.dataNode

import com.IceCreamQAQ.Yu.util.type.RelType

interface DataNode {

    fun asBoolean(): Boolean
    fun asByte(): Byte
    fun asShort(): Short
    fun asInt(): Int
    fun asLong(): Long
    fun asFloat(): Float
    fun asDouble(): Double
    fun asChar(): Char
    fun asString(): String

    fun <T> asEnum(enumClass: RelType<T>): T
    fun <T> asObject(type: RelType<T>): T
    fun <T> asArray(type: RelType<T>): List<T>
    fun <T> asMap(type: RelType<T>): Map<String, T>
}



//class ObjectNode() : ConfigNode
//class ArrayNode() : ConfigNode

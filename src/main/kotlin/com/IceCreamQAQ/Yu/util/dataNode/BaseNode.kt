package com.IceCreamQAQ.Yu.util.dataNode

import com.IceCreamQAQ.Yu.util.type.RelType

abstract class BaseNode : DataNode {

    override fun <T> asObject(type: RelType<T>): T {
        return if (type.realClass.isEnum) asEnum(type)
        else when (type.realType) {
                Boolean::class.java, Boolean::class.javaObjectType -> asBoolean()
                Byte::class.java, Byte::class.javaObjectType -> asByte()
                Short::class.java, Short::class.javaObjectType -> asShort()
                Int::class.java, Int::class.javaObjectType -> asInt()
                Long::class.java, Long::class.javaObjectType -> asLong()
                Float::class.java, Float::class.javaObjectType -> asFloat()
                Double::class.java, Double::class.javaObjectType -> asDouble()
                Char::class.java, Char::class.javaObjectType -> asChar()
                String::class.java, String::class.javaObjectType -> asString()
                else -> castTo(type)
            } as T
    }

    abstract fun <T> castTo(type: RelType<T>): T

}
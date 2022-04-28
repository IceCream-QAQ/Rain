package com.IceCreamQAQ.Yu.util.type

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RelType<T>(
    val realType: Type,
    val realClass: Class<T>
) {

    companion object {
        fun create(type: Type) =
            when (type) {
                is Class<*> -> RelType(type, type)
                is ParameterizedType -> RelType(type, type.rawType as Class<*>)
                else -> error("在尝试构建 RelType 时，遇到无法解析的类型，在 $type。")
            }
    }

    fun isAssignableFrom(otherType: RelType<*>): Boolean = realClass.isAssignableFrom(otherType.realClass)
}
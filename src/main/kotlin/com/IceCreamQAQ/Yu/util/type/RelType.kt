package com.IceCreamQAQ.Yu.util.type

import com.IceCreamQAQ.Yu.arrayMap
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RelType<T>(
    val realType: Type,
    val realClass: Class<T>,
    val generics: Array<RelType<*>>?
) {

    companion object {
        fun create(type: Type): RelType<*> =
            when (type) {
                is Class<*> -> RelType(type, type, null)
                is ParameterizedType -> RelType(
                    type,
                    type.rawType as Class<*>,
                    type.actualTypeArguments.arrayMap { create(it) })

                else -> error("在尝试构建 RelType 时，遇到无法解析的类型，在 $type。")
            }

        fun <T> create(clazz: Class<T>): RelType<T> = RelType(clazz, clazz, null)
    }

    fun isAssignableFrom(otherType: RelType<*>): Boolean = realClass.isAssignableFrom(otherType.realClass)
}
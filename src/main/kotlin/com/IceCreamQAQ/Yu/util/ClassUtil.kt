package com.IceCreamQAQ.Yu.util

object ClassUtil {
    fun isTypeOf(clazz: Class<*>, parentType: Class<*>): Boolean =
        parentType::class.java.isAssignableFrom(clazz)
}
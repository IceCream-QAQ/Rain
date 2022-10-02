package com.IceCreamQAQ.Yu.util.classMaker

abstract class MAnnotation<T>(val annotationType: Class<out Annotation>) {
    abstract fun add(key: String, value: Any)
}

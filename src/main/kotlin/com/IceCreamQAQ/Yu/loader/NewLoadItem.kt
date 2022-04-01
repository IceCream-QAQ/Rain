package com.IceCreamQAQ.Yu.loader

data class NewLoadItem(
    val clazz: Class<*>,
    val target: Class<*>,
    val annotation: Annotation?,
    val loadByAnnotation: Boolean = annotation != null
)
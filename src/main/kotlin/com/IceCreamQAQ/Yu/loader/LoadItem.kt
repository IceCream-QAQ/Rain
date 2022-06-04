package com.IceCreamQAQ.Yu.loader

data class LoadItem(
    val clazz: Class<*>,
    val target: Class<*>,
    val annotation: Annotation?,
    val loadByAnnotation: Boolean = annotation != null
)
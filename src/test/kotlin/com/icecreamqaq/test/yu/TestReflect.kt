package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.controller.DefaultControllerLoaderImpl
import com.IceCreamQAQ.Yu.nameWithParams

fun main() {
    var clazz: Class<*>? = DefaultControllerLoaderImpl::class.java
    while (clazz != null) {
        println("Class: ${clazz.name}")
        println("Fields: ")
        clazz.declaredFields.forEach { println("\t${it.name}: ${it.type.name}") }
        println("Methods: ")
        clazz.declaredMethods.forEach { println("\t${it.nameWithParams}") }
        clazz = clazz.superclass
        println("------------------------------------")
    }

    println(DefaultControllerLoaderImpl::class.java.getMethod("getSeparationCharacter"))
}
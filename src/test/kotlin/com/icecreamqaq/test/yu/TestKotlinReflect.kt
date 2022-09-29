package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.allField
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.controller.DefaultControllerLoaderImpl
import com.IceCreamQAQ.Yu.hasAnnotation
import javax.inject.Inject
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.kotlinProperty

class TestKotlinReflect {
    private lateinit var testField: String
    override fun toString(): String {
        return "TestKotlinReflect(testField='$testField')"
    }


}

fun main() {
    val clazz = DefaultControllerLoaderImpl::class

    val data = TestKotlinReflect()
    clazz.memberFunctions.forEach {
        println("-----------------------------------------")

        println(it.name)
        println(it.visibility)
        println(it.isAccessible)
        println(it.hasAnnotation<Inject>())
        println(it.returnType.hasAnnotation<Inject>())
        println(it.annotations)
//        println(it.javaField?.hasAnnotation<Inject>())
        println(it.hasAnnotation<Config>())

//        it.isAccessible = true
//        it.set(data, "444")
    }
    println("-----------------------------------------")

    clazz.java.allField.forEach {
        println(it.name)
        it.kotlinProperty?.let {
            println(it.name)
            println(it.visibility)
            println(it.isAccessible)
            println(it.hasAnnotation<Inject>())
            println(it.returnType.hasAnnotation<Inject>())
            println(it.annotations)
            println(it.javaField?.hasAnnotation<Inject>())
            println(it.hasAnnotation<Config>())
        }
    }

}
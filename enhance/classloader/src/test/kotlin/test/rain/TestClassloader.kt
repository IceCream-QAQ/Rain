package test.rain

import rain.classloader.AppClassloader

class TestClass(){
    fun test(){
        println("test")
    }
}

fun main(){
    val classloader = AppClassloader(Thread.currentThread().contextClassLoader)
    Thread.currentThread().contextClassLoader = classloader

    val tc = classloader.loadClass("test.rain.TestClass")
    tc.getMethod("test").invoke(tc.newInstance())
}
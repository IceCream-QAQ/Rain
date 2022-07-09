package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.di.config.impl.ConfigImpl


fun main() {

    val c = ConfigImpl(ConfigImpl::class.java.classLoader, null, null).init()
    println("ok")

}
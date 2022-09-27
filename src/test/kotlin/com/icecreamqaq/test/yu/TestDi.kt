package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.YuContext.Companion.get
import com.IceCreamQAQ.Yu.di.config.impl.ConfigImpl
import com.IceCreamQAQ.Yu.di.impl.ContextImpl
import com.IceCreamQAQ.Yu.event.EventBusImpl
import com.IceCreamQAQ.Yu.loader.AppLoader

fun main() {
    val context = ContextImpl(
        ContextImpl::class.java.classLoader,
        ConfigImpl(ConfigImpl::class.java.classLoader, null, null).init()
    ).init()
    val cc = context.findContext(EventBusImpl::class.java)
    val eventBus = cc.getBean()
    println(cc)
    println(eventBus)
    context.getBean(AppLoader::class.java).apply { println() }
}
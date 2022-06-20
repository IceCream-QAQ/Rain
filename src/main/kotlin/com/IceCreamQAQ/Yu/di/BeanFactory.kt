package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(BeanFactoryLoader::class)
@FunctionalInterface
interface BeanFactory<T> {

    // fun initClassContext(): ClassContext
    fun isSingleton() = true
    fun createBean(clazz: Class<T>, name: String): T?

}
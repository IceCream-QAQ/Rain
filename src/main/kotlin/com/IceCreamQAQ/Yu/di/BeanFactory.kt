package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(BeanFactoryLoader::class)
interface BeanFactory<T> {

    //    fun initClassContext():ClassContext
    val type: Class<T>
    fun isMulti() = false
    fun createBean(name: String): T?

}
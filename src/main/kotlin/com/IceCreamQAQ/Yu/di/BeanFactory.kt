package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(BeanFactoryLoader::class)
interface BeanFactory<T> {

//    fun initClassContext():ClassContext
    fun isMulti()= false
    fun createBean(clazz: Class<T>,name:String):T?

}
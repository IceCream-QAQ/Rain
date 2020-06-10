package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(BeanFactoryLoader::class)
interface BeanFactory<T> {

    fun createBean(clazz: Class<T>,name:String):T

}
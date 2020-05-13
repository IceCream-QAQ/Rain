package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.LoadBy_

@LoadBy_(BeanFactoryLoader::class)
interface BeanFactory<T> {

    fun createBean(clazz: Class<T>,name:String):T

}
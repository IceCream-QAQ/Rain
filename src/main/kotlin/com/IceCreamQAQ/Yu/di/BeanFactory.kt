package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.LoadBy_

@LoadBy_(BeanFactoryLoader::class)
interface BeanFactory<T> {

    fun createBean(name:String):T

}
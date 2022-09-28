package com.IceCreamQAQ.Yu.di

interface BeanFactory<T> {

    val type: Class<T>
    fun isMulti() = false
    fun createBean(name: String): T?

}
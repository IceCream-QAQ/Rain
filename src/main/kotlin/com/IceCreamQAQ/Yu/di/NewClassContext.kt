package com.IceCreamQAQ.Yu.di

interface NewClassContext<T> {

    val clazz: Class<T>
    val name: String
        get() = clazz.name

    val multi: Boolean
    val instanceAble: Boolean
    val bindAble: Boolean

    operator fun get(name: String): T? = getBean(name)
    operator fun set(name: String, instance: T): T = putBean(name, instance)

    fun newBean(): T
    fun getBean(): T?
    fun getBean(name: String = YuContext.defaultInstanceName): T?
    fun putBean(name: String = YuContext.defaultInstanceName, instance: T): T

    fun putBinds(name: String, cc: NewClassContext<out T>)

}
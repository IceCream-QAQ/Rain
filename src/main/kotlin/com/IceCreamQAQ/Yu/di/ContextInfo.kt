package com.IceCreamQAQ.Yu.di

import java.lang.reflect.Modifier

class ContextInfo<E>(
    val clazz: Class<E>,
    val context: ContextImpl
) {

    val isBean: Boolean
    val mulit: Boolean
    val isKotlin: Boolean

    var defaultInstance: E? = null
    lateinit var instances: MutableMap<String, E>



    var factory: BeanFactory<E>? = null

    var bindTo: MutableList<ContextInfo<in E>>? = null
    var binds: MutableMap<String, ContextInfo<out E>>? = null

    init {
        if (clazz.isInterface || Modifier.isAbstract(clazz.modifiers)) {
            isBean = true
            mulit = false
            isKotlin = KotlinContext::class.java.isAssignableFrom(clazz)



        } else {
            isBean = false
            mulit = false
            isKotlin = false
        }
    }


    fun putBind(name: String, context: ClassContext) {
        if (binds == null) binds = HashMap()
        binds!![name] = context
    }

    fun putInstance(name: String, instance: Any) {
        if (name == "" || (defaultInstance == null && !instances.containsKey(""))) defaultInstance = instance
        instances[name] = instance
    }

    fun getInstance(name: String?): Any? {
        return instances[name ?: return defaultInstance]
    }
}
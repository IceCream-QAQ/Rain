package com.IceCreamQAQ.Yu.di

interface YuContext {

    companion object {
        const val defaultInstanceName = ""
        const val din = defaultInstanceName

        inline operator fun <reified T> YuContext.get(instanceName: String = din): T? =
            getBean(T::class.java, instanceName)

        inline fun <reified T> YuContext.getBean(instanceName: String = din): T? =
            getBean(T::class.java, instanceName)

        inline operator fun <reified T> YuContext.set(instanceName: String = din, instance: T): T? =
            putBean(T::class.java, instanceName, instance)

        inline fun <reified T> YuContext.putBean(instanceName: String = din, instance: T): T? =
            putBean(T::class.java, instanceName, instance)

        operator fun <T> YuContext.get(clazz: Class<T>): T? = getBean(clazz)
    }

    val configManager: ConfigManager

    fun <T> getBean(clazz: Class<T>): T? = getBean(clazz, din)
    fun <T> getBean(clazz: Class<T>, instanceName: String): T?

    fun <T> putBean(clazz: Class<T>, instance: T): T = putBean(clazz, din, instance)
    fun <T> putBean(clazz: Class<T>, instanceName: String, instance: T): T

    fun <T> newBean(clazz: Class<T>): T
    fun <T> newBean(clazz: Class<T>, instanceName: String): T

    fun <T> injectBean(bean: T): T

}



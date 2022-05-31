package com.IceCreamQAQ.Yu.di

interface YuContext {

    companion object {
        const val defaultInstanceName = ""

        inline operator fun <reified T> YuContext.get(instanceName: String = defaultInstanceName): T? =
            getBean(T::class.java, instanceName)

        inline fun <reified T> YuContext.getBean(instanceName: String = defaultInstanceName): T? =
            getBean(T::class.java, instanceName)

        inline operator fun <reified T> YuContext.set(instanceName: String = defaultInstanceName, instance: T): T? =
            putBean(T::class.java, instanceName, instance)

        inline fun <reified T> YuContext.putBean(instanceName: String = defaultInstanceName, instance: T): T? =
            putBean(T::class.java, instanceName, instance)

        operator fun <T> YuContext.get(clazz: Class<T>): T? = getBean(clazz)
    }

    val configManager: OldConfigManager

    fun <T> getBean(clazz: Class<T>): T? = getBean(clazz, din)
    fun <T> getBean(clazz: Class<T>, instanceName: String): T?

    fun <T> putBean(clazz: Class<T>, instance: T): T = putBean(clazz, din, instance)
    fun <T> putBean(clazz: Class<T>, instanceName: String, instance: T): T

    fun <T> newBean(clazz: Class<T>): T

    fun <T: Any> injectBean(bean: T): T
    fun registerClass(clazz: Class<*>)
    fun registerClass(context: ClassContext<*>)

}



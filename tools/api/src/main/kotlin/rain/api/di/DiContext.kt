package rain.api.di

interface DiContext {
    companion object {
        const val defaultInstanceName = ""

        inline operator fun <reified T> DiContext.get(instanceName: String = defaultInstanceName): T? =
            getBean(T::class.java, instanceName)

        inline fun <reified T> DiContext.getBean(instanceName: String = defaultInstanceName): T? =
            getBean(T::class.java, instanceName)

        inline operator fun <reified T> DiContext.set(instanceName: String = defaultInstanceName, instance: T): T? =
            putBean(T::class.java, instanceName, instance)

        inline fun <reified T> DiContext.putBean(instanceName: String = defaultInstanceName, instance: T): T? =
            putBean(T::class.java, instanceName, instance)

        operator fun <T> DiContext.get(clazz: Class<T>): T? = getBean(clazz)
    }
    fun <T> getBean(clazz: Class<T>): T? = getBean(clazz, defaultInstanceName)
    fun <T> getBean(clazz: Class<T>, instanceName: String): T?

    fun <T> putBean(clazz: Class<T>, instance: T): T = putBean(clazz, defaultInstanceName, instance)
    fun <T> putBean(clazz: Class<T>, instanceName: String, instance: T): T

    fun <T> newBean(clazz: Class<T>): T

    fun <T : Any> injectBean(bean: T): T
    fun <T : Any> forceInjectBean(bean: T): T

    fun registerClass(clazz: Class<*>)
}
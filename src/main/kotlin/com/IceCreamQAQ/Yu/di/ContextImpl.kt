package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation
import java.lang.reflect.Constructor
import java.lang.reflect.Type
import javax.inject.Inject
import kotlin.reflect.KVisibility
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor

open class ContextImpl(
    val classloader: ClassLoader,
    override val configManager: ConfigManager
) : YuContext {

    open val contextMap: MutableMap<Class<*>, NewClassContext<*>> = HashMap()
    open val beanCreatorMap: MutableMap<Class<*>, BeanCreator<*>> = HashMap()
    open val beanInjectorMap: MutableMap<Class<*>, BeanInjector<*>> = HashMap()

    open fun <T> findContext(clazz: Class<T>): NewClassContext<T>? =
        (contextMap[clazz] as? NewClassContext<T>)

    override fun <T> getBean(clazz: Class<T>, instanceName: String): T? =
        findContext(clazz)?.getBean(instanceName)

    override fun <T> putBean(clazz: Class<T>, instanceName: String, instance: T): T =
        findContext(clazz)!!.putBean(instanceName, instance)

    override fun <T> newBean(clazz: Class<T>): T =
        findContext(clazz)!!.newBean()

    override fun <T : Any> injectBean(bean: T): T =
        getBeanInjector(bean.javaClass).invoke(bean)

    open fun getDataReader(type: Type): DataReader<*> {
        TODO()
    }

    open fun getConfigReader(type: Type): ConfigReader<*> {
        TODO()
    }

    inline fun <reified T : Any> getBeanCreator(): BeanCreator<T> = getBeanCreator(T::class.java)
    open fun <T : Any> getBeanCreator(clazz: Class<T>): BeanCreator<T> =
        beanCreatorMap.getOrPut(clazz) {
            val hasInjectOnClass = clazz.annotation<Inject>() != null
            val isKClass = clazz.annotation<Metadata>() != null

            var constructor: Constructor<T>? = null
            if (hasInjectOnClass && isKClass)
                constructor = clazz.kotlin
                    .primaryConstructor
                    ?.let { if (it.visibility == KVisibility.PUBLIC) it else null }
                    ?.javaConstructor

            var defaultConstructor: Constructor<T>? = null
            clazz.constructors.forEach {
                it as Constructor<T>
                if (it.parameters.isEmpty()) defaultConstructor = it
                it.annotation<Inject> { constructor = it }
            }

            constructor?.let { InjectConstructorBeanCreator(this, it) }
                ?: defaultConstructor?.let { DefaultConstructorBeanCreator(it) }
                ?: NoPublicConstructorBeanCreator(clazz)
        } as BeanCreator<T>

    inline fun <reified T> getBeanInjector(): BeanInjector<T> = getBeanInjector(T::class.java)
    open fun <T> getBeanInjector(clazz: Class<T>): BeanInjector<T> {
        TODO()
    }

}
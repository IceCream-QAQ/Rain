package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.error.BeanCreateError
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class BeanFactoryManager {

    private val factories: MutableMap<String, BeanFactory<*>> = ConcurrentHashMap()

    @Inject
    private lateinit var context: YuContext

    fun registerFactory(clazz: Class<BeanFactory<*>>) {
        val factory = context[clazz] ?: throw BeanCreateError("Cant Instanced BeanFactory ${clazz.name}")

        for (gi in clazz.genericInterfaces) {
            val pi = gi as? ParameterizedType ?: continue
            if (pi.rawType == BeanFactory::class.java) {
                val beanClass = pi.actualTypeArguments[0] as? Class<*>
                        ?: (pi.actualTypeArguments[0] as ParameterizedType).rawType as Class<*>
                factories[beanClass.name] = factory

                context.register(ClassContext(beanClass.name, beanClass, factory.isMulti(), factory))

            }
        }
    }

    operator fun get(clazz: String): BeanFactory<*>? {
        return getFactory(clazz)
    }

    fun <T> getFactory(clazz: Class<T>): BeanFactory<T>? {
        return getFactory(clazz.name) as BeanFactory<T>?
    }

    fun getFactory(clazz: String): BeanFactory<Any>? {
        return factories[clazz] as BeanFactory<Any>?
    }

}
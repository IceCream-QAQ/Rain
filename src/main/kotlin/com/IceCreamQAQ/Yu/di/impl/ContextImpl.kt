package com.IceCreamQAQ.Yu.di.impl

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.CreateByPrimaryConstructor
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.*
import com.IceCreamQAQ.Yu.di.config.ConfigManager
import com.IceCreamQAQ.Yu.di.config.ConfigReader
import com.IceCreamQAQ.Yu.hasAnnotation
import com.IceCreamQAQ.Yu.util.type.RelType
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

    open val contextMap: MutableMap<Class<*>, ClassContext<*>> = HashMap()
    open val dataReaderFactory: DataReaderFactory = ObjectDataReaderFactory(this, RelType.create(Any::class.java))

    open fun init(): ContextImpl {
        contextMap[ClassLoader::class.java] = NoInstanceClassContext(ClassLoader::class.java)
            .apply {
                putBinds("appClassloader", LocalInstanceClassContext(classloader))
            }

        contextMap[YuContext::class.java] = NoInstanceClassContext(YuContext::class.java)
            .apply {
                putBinds(din, LocalInstanceClassContext(this@ContextImpl).apply {
                    contextMap[ContextImpl::class.java] = this
                })
            }
        contextMap[ConfigManager::class.java] = NoInstanceClassContext(ConfigManager::class.java)
            .apply {
                putBinds(din, LocalInstanceClassContext(configManager).apply {
                    contextMap[configManager::class.java] = this
                })
            }

//        contextMap[YuContext::class.java] = LocalInstanceClassContext(this)
//        contextMap[ConfigManager::class.java] = LocalInstanceClassContext(configManager)
        return this
    }

    open fun <T> findContextOrNull(clazz: Class<T>): ClassContext<T>? = contextMap[clazz] as? ClassContext<T>
    open fun <T> findContext(clazz: Class<T>): ClassContext<T> =
        contextMap.getOrPut(clazz) {
            if (clazz.isBean) {
                clazz as Class<Any>
                InstanceAbleClassContext(this, clazz)
            } else NoInstanceClassContext(clazz)
        } as ClassContext<T>

    override fun <T> getBean(clazz: Class<T>, instanceName: String): T? =
        findContext(clazz).getBean(instanceName)

    override fun <T> putBean(clazz: Class<T>, instanceName: String, instance: T): T =
        findContext(clazz).putBean(instanceName, instance)

    override fun <T> newBean(clazz: Class<T>): T =
        findContext(clazz).newBean()

    override fun <T : Any> injectBean(bean: T): T =
        getBeanInjector(bean.javaClass).invoke(bean)

    override fun <T : Any> forceInjectBean(bean: T): T =
        ((findContextOrNull(bean.javaClass) as? InstanceAbleClassContext<T>)?.run { recreateBeanInjector() }
            ?: makeBeanInjector(bean.javaClass)).invoke(bean)

    override fun registerClass(clazz: Class<*>) {
        findContext(clazz)
    }

    override fun registerClass(context: ClassContext<*>) {
        contextMap[context.clazz] = context
    }

    open fun getDataReader(type: Type): DataReader<*> = dataReaderFactory(RelType.create(type))

    open fun getConfigReader(type: Type): ConfigReader<*> {
        TODO()
    }

    inline fun <reified T : Any> getBeanCreator(): BeanCreator<T> = getBeanCreator(T::class.java)
    open fun <T : Any> getBeanCreator(clazz: Class<T>): BeanCreator<T> =
        findContext(clazz).creator

    inline fun <reified T> getBeanInjector(): BeanInjector<T> = getBeanInjector(T::class.java)
    open fun <T> getBeanInjector(clazz: Class<T>) =
        findContext(clazz).injector

    open fun <T : Any> makeBeanCreator(clazz: Class<T>): BeanCreator<T> {
        val createByPrimaryConstructor = !clazz.hasAnnotation<NotSearch>()
        val isKClass = clazz.hasAnnotation<Metadata>()

        var constructor: Constructor<T>? = null
        if (createByPrimaryConstructor && isKClass)
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

        return constructor?.let { InjectConstructorBeanCreator(this, it) }
            ?: defaultConstructor?.let { DefaultConstructorBeanCreator(it) }
            ?: NoPublicConstructorBeanCreator(clazz)
    }

    open fun <T : Any> makeBeanInjector(clazz: Class<T>): BeanInjector<T> =
        ReflectBeanInject(this, clazz)

}
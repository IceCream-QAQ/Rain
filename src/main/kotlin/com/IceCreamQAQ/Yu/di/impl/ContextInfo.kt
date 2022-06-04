package com.IceCreamQAQ.Yu.di.impl

import com.IceCreamQAQ.Yu.di.*


open class BeanFactoryClassContext<T>(
    open val context: ContextImpl,
    open val ctx: ClassContext<BeanFactory<T>>,
    override val clazz: Class<T>
) : ClassContext<T> {

    override val multi: Boolean
        get() = false
    override val instanceAble: Boolean
        get() = false
    override val bindAble: Boolean
        get() = false
    override val creator: BeanCreator<T>
        get() = error("您无法向一个由 BeanFactory(${clazz.name}) 管理的上下文 ($name) 中要求 creator。")
    override val injector: BeanInjector<T>
        get() = error("您无法向一个由 BeanFactory(${clazz.name}) 管理的上下文 ($name) 中要求 Injector。")

    override fun newBean(): T {
        error("您无法向一个由 BeanFactory(${clazz.name}) 管理的上下文 ($name) 中要求新实例。")
    }

    override fun getBean(): T? =
        getBean(din)

    private var _factory: BeanFactory<T>? = null
    open fun recreateBeanFactory(): BeanFactory<T> {
        return (ctx.newBean()).also { _factory = it }
    }

    private val factory: BeanFactory<T>
        get() = _factory ?: recreateBeanFactory()

    override fun getBean(name: String): T? = factory.createBean(name)
    override fun putBinds(name: String, cc: ClassContext<out T>) {
        error("您无法向一个由 BeanFactory(${factory::class.java.name}) 管理的上下文 (${this.name}) 中提交绑定类。")
    }

    override fun putBean(name: String, instance: T): T {
        error("您无法向一个由 BeanFactory(${factory::class.java.name}) 管理的上下文 (${this.name}) 中提交实例。")
    }
}

abstract class BindableClassContext<T> : ClassContext<T> {
    override val bindAble: Boolean
        get() = true

    companion object {
        fun <T> BindableClassContext<T>.putBinds(binds: Map<String, ClassContext<*>>) {
            (binds as Map<String, ClassContext<T>>).forEach { (named, ctx) ->
                putBinds(named, ctx)
            }
        }
    }

    protected open val bindMap: MutableMap<String, ClassContext<out T>> = HashMap()

    override fun putBinds(name: String, cc: ClassContext<out T>) {
        bindMap[name] = cc
    }
}

open class NoInstanceClassContext<T>(override val clazz: Class<T>) : BindableClassContext<T>() {

    override val multi: Boolean
        get() = false
    override val instanceAble: Boolean
        get() = false
    override val creator: BeanCreator<T>
        get() = error("Class: $name, 没有 Creator！")
    override val injector: BeanInjector<T>
        get() = error("Class: $name, 没有 Injector！")

    override fun newBean(): T {
        error("Class: $name, 无法创建新实例！")
    }

    override fun getBean(): T? =
        bindMap[din]?.getBean()

    override fun getBean(name: String): T? =
        bindMap[name]?.getBean()

    override fun putBean(name: String, instance: T): T {
        error("Class: $name, 无法提交实例！")
    }
}

open class InstanceAbleClassContext<T>(
    val context: ContextImpl,
    override val clazz: Class<T>
) : BindableClassContext<T>() {

    private var _creator: BeanCreator<T>? = null
    open fun recreateBeanCreator(): BeanCreator<T> {
        return (context.makeBeanCreator(clazz as Class<Any>) as BeanCreator<T>).also { _creator = it }
    }

    private var _injector: BeanInjector<T>? = null
    open fun recreateBeanInjector(): BeanInjector<T> {
        return (context.makeBeanInjector(clazz as Class<Any>) as BeanInjector<T>).also { _injector = it }
    }

    override val creator: BeanCreator<T>
        get() = _creator ?: recreateBeanCreator()

    override val injector: BeanInjector<T>
        get() = _injector ?: recreateBeanInjector()

    override val multi: Boolean
        get() = false
    override val instanceAble: Boolean
        get() = true

    var defaultInstance: T? = null
    var instanceMap: MutableMap<String, T> = HashMap()

    override fun getBean(): T? =
        defaultInstance

    override fun getBean(name: String): T? =
        if (name == din) defaultInstance else instanceMap[name]

    override fun newBean(): T =
        injector(creator())

    override fun putBean(name: String, instance: T): T {
        if (name == din) defaultInstance = instance
        else instanceMap[name] = instance
        return instance
    }

}

open class LocalInstanceClassContext<T : Any>(
    val instance: T
) : ClassContext<T> {
    override val clazz: Class<T> = instance.javaClass
    override val multi: Boolean = false
    override val instanceAble: Boolean = false
    override val bindAble: Boolean = false
    override val creator: BeanCreator<T>
        get() = error("Local 实例 ${clazz.name} 无法提供 Creator!")
    override val injector: BeanInjector<T>
        get() = error("Local 实例无法提供 Injector!")

    override fun newBean(): T = error("Local 实例 ${clazz.name} 无法创建新的 Bean!")

    override fun getBean(): T? = instance

    override fun getBean(name: String): T? =
        if (name == din) instance else error("Local 实例 ${clazz.name} 无法提供名为 $name 的 Bean!")

    override fun putBean(name: String, instance: T): T = error("Local 实例 ${clazz.name} 无法保存新的 Bean!")

    override fun putBinds(name: String, cc: ClassContext<out T>) {
        error("Local 实例 ${clazz.name} 无法关联类型 $name: ${cc.clazz.name}!")
    }
}
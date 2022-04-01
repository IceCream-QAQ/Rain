package com.IceCreamQAQ.Yu.di


class BeanFactoryClassContext<T>(
    val factory: BeanFactory<T>
) : NewClassContext<T> {


    override val clazz: Class<T> = factory.type
    override val multi: Boolean
        get() = factory.isMulti()
    override val instanceAble: Boolean
        get() = false
    override val bindAble: Boolean
        get() = false

    override fun newBean(): T {
        error("您无法向一个由 BeanFactory(${factory::class.java.name}) 管理的上下文 ($name) 中要求新实例。")
    }

    override fun getBean(): T? =
        getBean(din)

    override fun getBean(name: String): T? = factory.createBean(name)
    override fun putBinds(name: String, cc: NewClassContext<out T>) {
        error("您无法向一个由 BeanFactory(${factory::class.java.name}) 管理的上下文 (${this.name}) 中提交绑定类。")
    }

    override fun putBean(name: String, instance: T): T {
        error("您无法向一个由 BeanFactory(${factory::class.java.name}) 管理的上下文 (${this.name}) 中提交实例。")
    }
}

abstract class BindableClassContext<T> : NewClassContext<T> {
    override val bindAble: Boolean
        get() = true

    open val bindMap: MutableMap<String, NewClassContext<out T>> = HashMap()

    override fun putBinds(name: String, cc: NewClassContext<out T>) {
        bindMap[name] = cc
    }
}

open class NoInstanceClassContext<T>(override val clazz: Class<T>) : BindableClassContext<T>() {

    override val multi: Boolean
        get() = false
    override val instanceAble: Boolean
        get() = false

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
    override val clazz: Class<T>,
    open val beanCreator: BeanCreator<T>,
    open val beanInjector: BeanInjector<T>,
) : BindableClassContext<T>() {

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
        beanInjector(beanCreator())

    override fun putBean(name: String, instance: T): T {
        if (name == din) defaultInstance = instance
        else instanceMap[name] = instance
        return instance
    }

}
package com.IceCreamQAQ.Yu.di

/**
 * 对象容器，管理具体的某种Class类型的实例
 *
 * @constructor 创建一个新的容器
 * @param name 容器名
 * @param clazz 具体的Class类型
 * @param singleton 是否为单例
 * @param factory 对象工厂
 * @param instances 实际存储对象的map
 * @param bindTo 被其他容器绑定的列表
 * @param binds 绑定的容器映射
 */
data class ClassContext(
    val name: String,
    val clazz: Class<*>,
    var singleton: Boolean,
    val factory: BeanFactory<*>? = null,
    var defaultInstance: Any? = null,
    val instances: MutableMap<String, Any> = HashMap(),
    var bindTo : MutableList<ClassContext>? = null,
    var binds: MutableMap<String, ClassContext>? = null
) {
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
package com.IceCreamQAQ.Yu.di

data class ClassContext(
        val name: String,
        val clazz: Class<*>,
        var multi: Boolean,
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
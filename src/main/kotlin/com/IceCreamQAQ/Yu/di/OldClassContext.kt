package com.IceCreamQAQ.Yu.di

data class OldClassContext(
    val name: String,
    val clazz: Class<*>,
    var multi: Boolean,
    val factory: BeanFactory<*>? = null,
    var defaultInstance: Any? = null,
    val instances: MutableMap<String, Any> = HashMap(),
    var bindTo : MutableList<OldClassContext>? = null,
    var binds: MutableMap<String, OldClassContext>? = null
) {
    fun putBind(name: String, context: OldClassContext) {
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
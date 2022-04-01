package com.IceCreamQAQ.Yu.di
//
//import kotlin.properties.ReadWriteProperty
//import kotlin.reflect.KProperty
//
//var context: YuContext? = null
//
//class ValueObj<T>(var obj: T) : ReadWriteProperty<Any, T> {
//    override fun getValue(thisRef: Any, property: KProperty<*>) = obj
//
//    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
//        obj = value
//    }
//}
//
//class MultiModeNotSupport<T> : ReadWriteProperty<Any, T> {
//    override fun getValue(thisRef: Any, property: KProperty<*>): T {
//        error("当 YuContext 不处于 single 模式时，不允许 Kotlin inject 方式注入！")
//    }
//
//    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
//        error("当 YuContext 不处于 single 模式时，不允许 Kotlin inject 方式注入！")
//    }
//
//}
//
//inline fun <reified T> inject(name: String = ""): ReadWriteProperty<Any, T> =
//    if (context == null) MultiModeNotSupport()
//    else ValueObj(context!!.getBean(T::class.java, name)!!)
//
//inline fun <reified T> config(name: String): ReadWriteProperty<Any, T> =
//    if (context == null) MultiModeNotSupport()
//    else ValueObj(context!!.configManager.get(name, T::class.java)!!)
//
//inline fun <reified T> configArray(name: String): ReadWriteProperty<Any, List<T>> =
//    if (context == null) MultiModeNotSupport()
//    else ValueObj(context!!.configManager.getArray(name, T::class.java)!!)
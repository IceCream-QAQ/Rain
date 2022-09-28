package com.IceCreamQAQ.Yu.di

import java.lang.reflect.Modifier

internal const val din = YuContext.defaultInstanceName
internal val Class<*>.isBean: Boolean get() = !(this.isInterface || Modifier.isAbstract(this.modifiers))

inline fun <reified T> YuContext.getBean(name: String = YuContext.defaultInstanceName, nullFun: () -> T): T {
    return getBean(T::class.java, name) ?: nullFun()
}

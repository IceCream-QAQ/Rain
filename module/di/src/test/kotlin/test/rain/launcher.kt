package test.rain

import rain.di.Config
import rain.di.YuContext.Companion.get
import rain.di.config.impl.ConfigImpl
import rain.di.impl.ContextImpl

class TestBean(@Config("test.named") val name: String)

fun main() {
    val config = ConfigImpl(Thread.currentThread().contextClassLoader, null, null)
        .apply { init() }
    val context = ContextImpl(Thread.currentThread().contextClassLoader, config)
        .apply { init() }

    println(context.get<TestBean>()!!.name)
}
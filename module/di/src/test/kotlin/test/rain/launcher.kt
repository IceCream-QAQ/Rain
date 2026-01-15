package test.rain

import rain.api.di.DiContext.Companion.get
import rain.di.Config
import rain.di.config.impl.ConfigImpl
import rain.di.impl.ContextImpl

class TestBean(@Config("test.named") val name: String) {
    companion object {
        @Config("test.named")
        lateinit var name: String
    }
}

object TestObject {
    @Config("test.named")
    lateinit var name: String
}

fun main() {
    val config = ConfigImpl(Thread.currentThread().contextClassLoader, null, null)
        .apply { init() }
    val context = ContextImpl(Thread.currentThread().contextClassLoader, config)
        .apply { init() }

    println(context.get<TestBean>()!!.name)
    println(context.get<TestObject>()!!.name)
    println(context.get<TestBean.Companion>()!!.name)
}
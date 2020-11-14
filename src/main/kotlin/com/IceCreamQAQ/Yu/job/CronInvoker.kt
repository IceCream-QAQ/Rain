package com.IceCreamQAQ.Yu.job

import java.lang.reflect.Method

interface CronInvoker {
    val name:String
    operator fun invoke()
}

class ReflectCronInvoker(val instance: Any, val method: Method) : CronInvoker {
    override val name = "${instance::class.java.name}.${method.name}"

    override fun invoke() {
        method.invoke(instance)
    }
}
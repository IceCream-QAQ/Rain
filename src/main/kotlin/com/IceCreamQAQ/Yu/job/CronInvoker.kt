package com.IceCreamQAQ.Yu.job

import java.lang.reflect.Method

interface CronInvoker {
    fun invoker()
}

class ReflectCronInvoker(val instance: Any, val method: Method) : CronInvoker {

    override fun invoker() {
        method.invoke(instance)
    }
}
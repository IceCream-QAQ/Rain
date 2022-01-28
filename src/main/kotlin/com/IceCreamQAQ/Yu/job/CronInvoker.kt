package com.IceCreamQAQ.Yu.job

import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend

interface CronInvoker {
    suspend operator fun invoke()
}

class ReflectCronInvoker(val instance: Any, val method: Method) : CronInvoker {

    override suspend fun invoke() {
        method.invoke(instance)
    }

}

open class KFunReflectCronInvoker(val instance: Any, val method: KFunction<*>) : CronInvoker {

    override suspend fun invoke() {
        method.call(instance)
    }

}

open class KSuspendFunReflectCronInvoker(val instance: Any, val method: KFunction<*>) : CronInvoker {

    override suspend fun invoke() {
        method.callSuspend(instance)
    }

}

open class DslCronInvoker(private val function: suspend () -> Unit) : CronInvoker {
    override suspend fun invoke() {
        function()
    }

}
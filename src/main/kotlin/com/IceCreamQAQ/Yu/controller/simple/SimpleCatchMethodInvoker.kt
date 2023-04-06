package com.IceCreamQAQ.Yu.controller.simple

import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.controller.ProcessInvoker

class SimpleCatchMethodInvoker<CTX : ActionContext>(
    val errorType: Class<out Throwable>,
    val invoker: ProcessInvoker<CTX>
) : ProcessInvoker<CTX> {

    override suspend fun invoke(context: CTX): Any? {
        if (context.runtimeError == null || errorType.isAssignableFrom(context.runtimeError!!::class.java)) return null
        return invoker.invoke(context)
    }

}
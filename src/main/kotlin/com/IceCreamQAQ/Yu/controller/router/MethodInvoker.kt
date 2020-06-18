package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.controller.ActionContext

@Deprecated("已经弃用")
interface MethodInvoker {
    @Throws(Exception::class)
    fun invoke(context: ActionContext): Any?
}
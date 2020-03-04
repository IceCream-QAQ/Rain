package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.controller.ActionContext


interface MethodInvoker {
    @Throws(Exception::class)
    fun invoke(context: ActionContext): Any?
}
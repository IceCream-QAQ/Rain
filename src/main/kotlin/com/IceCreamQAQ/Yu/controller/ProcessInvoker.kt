package com.IceCreamQAQ.Yu.controller

interface ProcessInvoker<T : ActionContext> {

    operator fun invoke(context: T): Any

}
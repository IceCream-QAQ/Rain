package com.IceCreamQAQ.Yu.controller

open class ProcessInfo<T : ActionContext>(
    open val priority: Int,
    open val except: Array<String>,
    open val only: Array<String>,
    open val invoker: ProcessInvoker<T>,
)
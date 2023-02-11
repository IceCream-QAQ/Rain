package com.IceCreamQAQ.Yu.controller

class ProcessInfo<T : ActionContext>(
    val priority: Int,
    val except: Array<String>,
    val only: Array<String>,
    val invoker: ProcessInvoker<T>,
)
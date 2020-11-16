package com.IceCreamQAQ.Yu.controller

interface ActionInvoker : Router {
    val befores: Array<MethodInvoker>
    val invoker: MethodInvoker
    val afters: Array<MethodInvoker>
    val catchs: Array<CatchInvoker>
}

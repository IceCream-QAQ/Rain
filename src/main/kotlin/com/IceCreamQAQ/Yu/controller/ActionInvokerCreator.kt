package com.IceCreamQAQ.Yu.controller

fun interface ActionInvokerCreator<CTX : ActionContext> {

    operator fun invoke(): ActionInvoker<CTX>

}
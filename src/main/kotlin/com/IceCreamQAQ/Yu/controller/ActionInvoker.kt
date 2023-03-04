package com.IceCreamQAQ.Yu.controller

interface ActionInvoker<CTX : ActionContext> {

    operator fun invoke(context: CTX): Boolean

}
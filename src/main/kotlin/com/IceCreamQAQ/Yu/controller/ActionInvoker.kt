package com.IceCreamQAQ.Yu.controller

interface ActionInvoker<CTX : ActionContext> {

    suspend operator fun invoke(context: CTX): Boolean

}
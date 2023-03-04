package com.IceCreamQAQ.Yu.controller

open class RootRouter<CTX : ActionContext, ROT : Router>(
    val router: ROT,
    val actions: List<ActionInfo>
)
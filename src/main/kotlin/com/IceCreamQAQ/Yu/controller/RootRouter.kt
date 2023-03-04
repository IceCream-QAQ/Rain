package com.IceCreamQAQ.Yu.controller

open class RootRouter<ROT : Router>(
    val router: ROT,
    val actions: List<ActionInfo>
)
package com.IceCreamQAQ.Yu.controller

interface RouterInfo {
    val router: Router
    val interceptorInfo: InterceptorInfo
    val allAction: MutableList<ActionInfo>
}
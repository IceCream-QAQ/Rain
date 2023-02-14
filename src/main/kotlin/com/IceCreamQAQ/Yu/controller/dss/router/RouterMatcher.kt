package com.IceCreamQAQ.Yu.controller.dss.router

import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

fun interface RouterMatcher<CTX : PathActionContext> {

    operator fun invoke(path: String, context: CTX): Boolean

}
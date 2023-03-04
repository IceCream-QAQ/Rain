package com.IceCreamQAQ.Yu.controller.dss.router

import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

open class DynamicRouter<CTX : PathActionContext>(
    val matcher: RouterMatcher<CTX>,
    val router: DssRouter<CTX>
)
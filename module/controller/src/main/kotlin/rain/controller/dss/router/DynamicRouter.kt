package rain.controller.dss.router

import rain.controller.ActionInvoker
import rain.controller.dss.PathActionContext

open class DynamicRouter<
        CTX : PathActionContext,
        AI : ActionInvoker<CTX>,
        ROT : DssRouter<CTX, AI, *, *>,
        >(
    val matcher: RouterMatcher<CTX>,
    val router: ROT
)
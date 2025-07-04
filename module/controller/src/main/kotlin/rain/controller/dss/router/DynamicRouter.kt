package rain.controller.dss.router

import rain.controller.ActionInvoker
import rain.controller.dss.PathActionContext

open class DynamicRouter<CTX : PathActionContext, AI : ActionInvoker<CTX>>(
    val matcher: RouterMatcher<CTX>,
    val router: DssRouter<CTX, AI>
)
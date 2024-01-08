package rain.controller.dss.router

import rain.controller.dss.PathActionContext

open class DynamicRouter<CTX : PathActionContext>(
    val matcher: RouterMatcher<CTX>,
    val router: DssRouter<CTX>
)
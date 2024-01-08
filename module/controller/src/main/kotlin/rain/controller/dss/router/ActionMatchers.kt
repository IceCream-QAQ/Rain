package rain.controller.dss.router

import rain.controller.dss.PathActionContext

class StaticActionMatcher<CTX : PathActionContext>(val path: String): RouterMatcher<CTX> {
    override fun invoke(path: String?, context: CTX): Boolean {
        return path == this.path
    }

}
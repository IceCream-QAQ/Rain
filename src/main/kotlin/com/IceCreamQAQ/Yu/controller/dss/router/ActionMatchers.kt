package com.IceCreamQAQ.Yu.controller.dss.router

import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

class StaticActionMatcher<CTX : PathActionContext>(val path: String):RouterMatcher<CTX> {
    override fun invoke(path: String?, context: CTX): Boolean {
        return path == this.path
    }

}
package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.controller.ActionContextBase
import com.IceCreamQAQ.Yu.controller.route.RouteInvoker
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

open class DefaultRouter(val level: Int):RouterPlus {
    protected var needMatch: MutableMap<String, RouterPlus> = ConcurrentHashMap()
    var routers: MutableMap<String, RouterPlus> = ConcurrentHashMap()

    override fun invoke(path: String, context: ActionContext): Boolean {

        if (context.getPath().size == level) return false
        val nextPath = context.getPath()[level]
        if (routers[nextPath]?.invoke(nextPath, context) == true)return true
        else {
            for ((k, v) in needMatch) {
                if (Pattern.matches(k, path))
                    if (v.invoke(nextPath, context)) return true
            }
        }
        return false
    }

    fun putInvoker(router:DefaultRouter){
        this.needMatch.putAll(router.needMatch)
        this.routers.putAll(router.routers)
    }

}
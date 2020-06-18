package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.controller.ActionContext
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

@Deprecated("已经弃用")
open class DefaultRouter(val level: Int) : RouterPlus {
    val needMatch: MutableMap<String, RouterPlus> = ConcurrentHashMap()
    val routers: MutableMap<String, RouterPlus> = ConcurrentHashMap()

    override fun invoke(path: String, context: ActionContext): Boolean {

        if (context.path.size == level) return false
        val nextPath = context.path[level]
        if (routers[nextPath]?.invoke(nextPath, context) == true) return true
        else {
            for ((k, v) in needMatch) {
                if (Pattern.matches(k, path))
                    return v.invoke(nextPath, context)
            }
        }
        return false
    }

    fun putInvoker(router: DefaultRouter) {
        this.needMatch.putAll(router.needMatch)
        this.routers.putAll(router.routers)
    }

}
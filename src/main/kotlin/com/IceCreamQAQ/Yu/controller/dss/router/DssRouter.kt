package com.IceCreamQAQ.Yu.controller.dss.router

import com.IceCreamQAQ.Yu.controller.ActionInvoker
import com.IceCreamQAQ.Yu.controller.Router
import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

open class DssRouter<CTX : PathActionContext>(
    val level: Int
) : Router {

    val actions = ArrayList<ActionInvoker<PathActionContext>>()

    val staticSubrouter = HashMap<String, DssRouter<CTX>>()
    val dynamicSubrouter = ArrayList<DynamicRouter<CTX>>()

    operator fun invoke(context: CTX): Boolean {
        val path = context.path[level]
        if (staticSubrouter[path]?.invoke(context) == true) return true
        if (dynamicSubrouter.any { it.matcher(path, context) && it.router(context) }) return true
        if (actions.any { it.invoke(context) }) return true
        return false
    }

}
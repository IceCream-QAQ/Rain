package com.IceCreamQAQ.Yu.controller.dss.router

import com.IceCreamQAQ.Yu.controller.ActionInvoker
import com.IceCreamQAQ.Yu.controller.Router
import com.IceCreamQAQ.Yu.controller.dss.PathActionContext

open class DssRouter<CTX : PathActionContext>(
    val level: Int
) : Router {

    val staticSubrouter = HashMap<String, DssRouter<CTX>>()
    val dynamicSubrouter = ArrayList<DynamicRouter<CTX>>()

    val staticActions = HashMap<String, MutableList<ActionInvoker<PathActionContext>>>()
    val dynamicActions = ArrayList<Pair<RouterMatcher<CTX>, MutableList<ActionInvoker<PathActionContext>>>>()

    operator fun invoke(context: CTX): Boolean {
        val path = context.path[level]
        if (staticSubrouter[path]?.invoke(context) == true) return true
        if (dynamicSubrouter.any { it.matcher(path, context) && it.router(context) }) return true

        if (staticActions[path]?.any { it.invoke(context) } == true) return true
        if (dynamicActions.any { it.first(path, context) && it.second.any { action -> action(context) } }) return true
        return false
    }

}
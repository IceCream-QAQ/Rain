package com.IceCreamQAQ.Yu.controller.dss

import com.IceCreamQAQ.Yu.controller.ProcessInvoker
import com.IceCreamQAQ.Yu.controller.dss.router.RouterMatcher
import com.IceCreamQAQ.Yu.controller.simple.SimpleActionInvoker

open class DssActionInvoker<CTX : PathActionContext>(
    val level: Int,
    val matchers: List<RouterMatcher<CTX>>,
    action: ProcessInvoker<CTX>,
    beforeProcesses: Array<ProcessInvoker<CTX>>,
    aftersProcesses: Array<ProcessInvoker<CTX>>,
    catchsProcesses: Array<ProcessInvoker<CTX>>
) : SimpleActionInvoker<CTX>(action, beforeProcesses, aftersProcesses, catchsProcesses) {

    override fun invoke(context: CTX): Boolean {
        matchers.forEachIndexed { i, matcher ->
            if (!matcher(context.path[i + level],context)) return false
        }
        return super.invoke(context)
    }

}
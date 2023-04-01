package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.controller.ProcessInvoker
import com.IceCreamQAQ.Yu.controller.dss.DssActionInvoker
import com.IceCreamQAQ.Yu.controller.dss.router.RouterMatcher

class TestActionInvoker(
    val channels: List<String>,
    level: Int,
    matchers: List<RouterMatcher<TestActionContext>>,
    action: ProcessInvoker<TestActionContext>,
    beforeProcesses: Array<ProcessInvoker<TestActionContext>>,
    aftersProcesses: Array<ProcessInvoker<TestActionContext>>,
    catchsProcesses: Array<ProcessInvoker<TestActionContext>>
) : DssActionInvoker<TestActionContext>(level, matchers, action, beforeProcesses, aftersProcesses, catchsProcesses) {

    override suspend fun invoke(context: TestActionContext): Boolean {
        if (context.channel !in channels) return false
        return super.invoke(context)
    }

}
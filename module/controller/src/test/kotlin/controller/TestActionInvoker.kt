package controller

import rain.controller.ProcessInvoker
import rain.controller.dss.DssActionInvoker
import rain.controller.dss.router.RouterMatcher


class TestActionInvoker(
    val channels: List<String>,
    level: Int,
    matchers: List<RouterMatcher<TestActionContext>>,
    action: ProcessInvoker<TestActionContext>,
    beforeProcesses: Array<ProcessInvoker<TestActionContext>>,
    aftersProcesses: Array<ProcessInvoker<TestActionContext>>,
    catchsProcesses: Array<ProcessInvoker<TestActionContext>>
) : DssActionInvoker<TestActionContext>(level, matchers, action, beforeProcesses, aftersProcesses, catchsProcesses) {

    override suspend fun checkChannel(context: TestActionContext): Boolean {
        return context.channel in channels
    }

}
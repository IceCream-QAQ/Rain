package controller

import rain.controller.ProcessInvoker
import rain.controller.dss.DssActionInvoker
import rain.controller.dss.router.RouterMatcher
import rain.controller.simple.SimpleActionInvoker


class TestActionInvoker(
    action: ProcessInvoker<TestActionContext>,
    beforeProcesses: Array<ProcessInvoker<TestActionContext>>,
    aftersProcesses: Array<ProcessInvoker<TestActionContext>>,
    catchsProcesses: Array<ProcessInvoker<TestActionContext>>
) : SimpleActionInvoker<TestActionContext>(action, beforeProcesses, aftersProcesses, catchsProcesses) {

    override suspend fun checkChannel(context: TestActionContext): Boolean = true

}
package controller

import rain.controller.ProcessInvoker
import rain.controller.simple.SimpleActionInvoker
import java.lang.reflect.Method


class TestActionInvoker(
    override val actionClass: Class<*>?,
    override val actionMethod: Method?,
    action: ProcessInvoker<TestActionContext>,
    beforeProcesses: Array<ProcessInvoker<TestActionContext>>,
    aftersProcesses: Array<ProcessInvoker<TestActionContext>>,
    catchsProcesses: Array<ProcessInvoker<TestActionContext>>
) : SimpleActionInvoker<TestActionContext>(
    actionClass,
    actionMethod,
    action,
    beforeProcesses,
    aftersProcesses,
    catchsProcesses
) {

    override suspend fun checkChannel(context: TestActionContext): Boolean = true

}
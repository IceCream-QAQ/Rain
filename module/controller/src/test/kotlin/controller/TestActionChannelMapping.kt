package controller

import rain.controller.ActionInvoker

class TestActionChannelMapping: ActionInvoker<TestActionContext> {

    var test1: TestActionInvoker? = null
    var test2: TestActionInvoker? = null
    var test3: TestActionInvoker? = null
    var test4: TestActionInvoker? = null

    override suspend fun invoke(context: TestActionContext): Boolean {
        return when(context.channel){
            "test1" -> test1
            "test2" -> test2
            "test3" -> test3
            "test4" -> test4
            else -> null
        }?.invoke(context) ?: false
    }
}
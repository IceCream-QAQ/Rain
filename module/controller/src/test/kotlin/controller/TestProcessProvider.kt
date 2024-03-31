package controller

import rain.controller.ProcessInvoker
import rain.controller.ProcessProvider
import rain.controller.annotation.After
import java.lang.reflect.Method

@After(weight = 1)
class TestProcessProvider : ProcessProvider<TestActionContext> {
    override fun <T> invoke(
        provideAnnotation: Annotation,
        functionAnnotation: Annotation,
        controllerClass: Class<T>,
        controllerInstance: T,
        action: Method?
    ): ProcessInvoker<TestActionContext> = ProcessInvoker {
        println("TestProcess")
        return@ProcessInvoker null
    }
}
package controller

import rain.controller.ControllerInstanceGetter
import rain.controller.simple.SimpleKJReflectMethodInvoker
import java.lang.reflect.Method

open class TestMethodInvoker(
    method: Method,
    instance: ControllerInstanceGetter
) : SimpleKJReflectMethodInvoker<TestActionContext, TestActionContext.() -> Any?>(method, instance) {
    override fun initParam(method: Method, params: Array<MethodParam<TestActionContext.() -> Any?>>) {
        params.forEach { it.attachment = { saves[it.name] } }
    }

    override fun getParam(param: MethodParam<TestActionContext.() -> Any?>, context: TestActionContext): Any? =
        param.attachment?.invoke(context)

}
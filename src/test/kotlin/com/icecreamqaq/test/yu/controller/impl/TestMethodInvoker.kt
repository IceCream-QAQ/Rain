package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.controller.ControllerInstanceGetter
import com.IceCreamQAQ.Yu.controller.dss.PathActionContext
import com.IceCreamQAQ.Yu.controller.simple.SimpleKJReflectMethodInvoker
import java.lang.reflect.Method

open class TestMethodInvoker(
    method: Method,
    instance: ControllerInstanceGetter
) : SimpleKJReflectMethodInvoker<TestActionContext, TestActionContext.() -> Any?>(method, instance) {
    override fun initParam(params: Array<MethodParam<TestActionContext.() -> Any?>>) {
        params.forEach { it.attachment = { saves[it.name] } }
    }

    override fun getParam(param: MethodParam<TestActionContext.() -> Any?>, context: TestActionContext): Any? =
        param.attachment?.invoke(context)

}
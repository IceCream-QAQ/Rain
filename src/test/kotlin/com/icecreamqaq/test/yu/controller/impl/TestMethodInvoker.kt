package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.controller.ControllerInstanceGetter
import com.IceCreamQAQ.Yu.controller.dss.PathActionContext
import com.IceCreamQAQ.Yu.controller.simple.SimpleKJReflectMethodInvoker
import java.lang.reflect.Method

open class TestMethodInvoker(
    method: Method,
    instance: ControllerInstanceGetter
) : SimpleKJReflectMethodInvoker<PathActionContext, PathActionContext.() -> Any?>(method, instance) {
    override fun initParam(params: Array<MethodParam<PathActionContext.() -> Any?>>) {
        params.forEach { it.attachment = { saves[it.name] } }
    }

    override fun getParam(param: MethodParam<PathActionContext.() -> Any?>, context: PathActionContext): Any? =
        param.attachment?.invoke(context)

}
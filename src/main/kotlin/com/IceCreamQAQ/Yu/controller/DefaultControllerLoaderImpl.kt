package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.MethodInvoker
import com.IceCreamQAQ.Yu.controller.router.ReflectMethodInvoker
import java.lang.reflect.Method

open class DefaultControllerLoaderImpl :DefaultControllerLoader() {

    override fun createMethodInvoker_(obj: Any, method: Method): MethodInvoker {
        return ReflectMethodInvoker(method,obj)
    }

    override fun createActionInvoker_(level: Int, actionMethod: Method): DefaultActionInvoker {
        return DefaultActionInvoker(level)
    }

}
package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.DefaultRouter
import com.IceCreamQAQ.Yu.controller.router.MethodInvoker
import com.IceCreamQAQ.Yu.controller.router.ReflectMethodInvoker
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem_
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Named

open class DefaultControllerLoaderImpl :DefaultControllerLoader() {

    override fun createMethodInvoker_(obj: Any, method: Method): MethodInvoker {
        return ReflectMethodInvoker(method,obj)
    }

    override fun createActionInvoker_(level: Int, actionMethod: Method): DefaultActionInvoker {
        return DefaultActionInvoker(level)
    }

}
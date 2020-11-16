package com.IceCreamQAQ.Yu.controller

import java.lang.reflect.Method

open class DefaultControllerLoaderImpl : DefaultControllerLoader() {
    override fun createMethodInvoker(instance: Any, method: Method): MethodInvoker = ReflectMethodInvoker(method, instance)

    override fun createActionInvoker(level: Int, actionMethod: Method, instance: Any): DefaultActionInvoker = DefaultActionInvoker(level, actionMethod, instance)
}
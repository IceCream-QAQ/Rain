package com.IceCreamQAQ.Yu.controller.dss

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
import com.IceCreamQAQ.Yu.annotation.Path
import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.controller.dss.router.DssRouter
import com.IceCreamQAQ.Yu.di.YuContext
import java.lang.reflect.Method

/** 动静分离 ControllerLoader
 * 这是一个简单的 Controller 实现，用以技术性验证。
 * 这个简单的实现提供了基础的 Router 与 Action 实现，如果业务合适，可以直接基于本实现开展业务。
 *
 * 路由做了基础的静态匹配与动态匹配分离。
 */
abstract class DssControllerLoader<CTX : PathActionContext, ROT : DssRouter<CTX>, RootInfo : RootRouterProcessFlowInfo<CTX, ROT>, PI : ProcessInvoker<CTX>>(
    context: YuContext
) : ControllerLoader<CTX, ROT, RootInfo>(context) {


    override fun makeBefore(
        beforeAnnotation: Before,
        controllerClass: Class<*>,
        beforeMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        createMethodInvoker(controllerClass, beforeMethod, instanceGetter)
            ?.let { ProcessInfo(beforeAnnotation.weight, beforeAnnotation.except, beforeAnnotation.only, it) }


    override fun makeAfter(
        afterAnnotation: After,
        controllerClass: Class<*>,
        afterMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        createMethodInvoker(controllerClass, afterMethod, instanceGetter)
            ?.let { ProcessInfo(afterAnnotation.weight, afterAnnotation.except, afterAnnotation.only, it) }

    override fun makeCatch(
        catchAnnotation: Catch,
        controllerClass: Class<*>,
        catchMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        createCatchMethodInvoker(catchAnnotation.error.java, controllerClass, catchMethod, instanceGetter)
            ?.let { ProcessInfo(catchAnnotation.weight, catchAnnotation.except, catchAnnotation.only, it) }

    override fun controllerInfo(
        root: RootInfo,
        annotation: Annotation?,
        controllerClass: Class<*>,
        instanceGetter: ControllerInstanceGetter
    ): ControllerProcessFlowInfo<CTX,ROT>? {
        TODO("Not yet implemented")
    }


    override fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerProcessFlowInfo<CTX,ROT>,
        controllerClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ActionProcessFlowInfo<CTX>? {
        TODO("Not yet implemented")
    }


    abstract fun controllerChannel(annotation: Annotation?, controllerClass: Class<*>): Array<String>
    abstract fun actionChannel(actionMethod: Method): Array<String>?
    abstract fun createMethodInvoker(
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): PI?

    abstract fun createCatchMethodInvoker(
        throwableType: Class<out Throwable>,
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): PI?

}
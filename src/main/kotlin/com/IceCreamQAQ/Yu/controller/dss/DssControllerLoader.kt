package com.IceCreamQAQ.Yu.controller.dss

import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
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
abstract class DssControllerLoader<CTX : PathActionContext, ROT : DssRouter<CTX>, RootInfo : RootRouterProcessFlowInfo<CTX, ROT>>(
    context: YuContext
) : ControllerLoader<CTX, ROT, RootInfo>(context) {

    override fun findRootRouter(name: String): RootInfo? {
        TODO("Not yet implemented")
    }

    override fun makeBefore(
        beforeAnnotation: Before,
        controllerClass: Class<*>,
        beforeMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? {
        TODO("Not yet implemented")
    }

    override fun makeAfter(
        beforeAnnotation: After,
        controllerClass: Class<*>,
        afterMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? {
        TODO("Not yet implemented")
    }

    override fun makeCatch(
        beforeAnnotation: Catch,
        controllerClass: Class<*>,
        catchMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? {
        TODO("Not yet implemented")
    }

    override fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerProcessFlowInfo<CTX>,
        controllerClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ActionProcessFlowInfo<CTX>? {
        TODO("Not yet implemented")
    }

    override fun controllerInfo(
        root: RootInfo,
        controllerClass: Class<*>,
        instanceGetter: ControllerInstanceGetter
    ): ControllerProcessFlowInfo<CTX>? {
        TODO("Not yet implemented")
    }


}
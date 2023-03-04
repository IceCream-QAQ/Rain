package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.controller.dss.DssActionInvoker
import com.IceCreamQAQ.Yu.controller.dss.DssControllerLoader
import com.IceCreamQAQ.Yu.controller.dss.router.DssRouter
import com.IceCreamQAQ.Yu.controller.dss.router.DynamicRouter
import com.IceCreamQAQ.Yu.controller.dss.router.RouterMatcher
import com.IceCreamQAQ.Yu.di.YuContext
import java.lang.reflect.Method

class TestControllerLoader(
    context: YuContext
) : DssControllerLoader<TestActionContext, DssRouter<TestActionContext>, RootRouterProcessFlowInfo<TestActionContext, DssRouter<TestActionContext>>>(
    context
) {

    val root = RootRouterProcessFlowInfo<TestActionContext, DssRouter<TestActionContext>>(DssRouter(0))

    lateinit var rootRouter: RootRouter<TestActionContext,DssRouter<TestActionContext>>

    override fun findRootRouter(name: String): RootRouterProcessFlowInfo<TestActionContext, DssRouter<TestActionContext>> =
        root


    override fun getSubStaticRouter(
        router: DssRouter<TestActionContext>,
        subPath: String
    ): DssRouter<TestActionContext> =
        router.staticSubrouter.getOrPut(subPath) {
            DssRouter(router.level + 1)
        }

    override fun getSubDynamicRouter(
        router: DssRouter<TestActionContext>,
        matcher: RouterMatcher<TestActionContext>
    ): DssRouter<TestActionContext> =
        let {
            router.dynamicSubrouter.firstOrNull { it.matcher == matcher }
                ?: DynamicRouter(matcher, DssRouter(router.level + 1))
                    .apply { router.dynamicSubrouter.add(this) }
        }.router

    override fun controllerChannel(annotation: Annotation?, controllerClass: Class<*>): List<String> =
        arrayListOf("test1", "test2", "test3", "test4")

    override fun actionInfo(controllerChannel: List<String>, actionMethod: Method): Pair<String, List<String>>? {
        actionMethod.annotation<TestAction> { return value to channel.toList() }
        actionMethod.annotation<TestAction1> { return value to listOf("test1") }
        actionMethod.annotation<TestAction2> { return value to listOf("test2") }
        actionMethod.annotation<TestAction3> { return value to listOf("test3") }
        actionMethod.annotation<TestAction4> { return value to listOf("test4") }
        return null
    }

    override fun createMethodInvoker(
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<TestActionContext> =
        TestMethodInvoker(targetMethod, instanceGetter)

    override fun createCatchMethodInvoker(
        throwableType: Class<out Throwable>,
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<TestActionContext> =
        TestCatchMethodInvoker(targetMethod, instanceGetter, throwableType)

    override fun createActionInvoker(
        channels: List<String>,
        level: Int,
        matchers: List<RouterMatcher<TestActionContext>>,
        actionClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter,
        beforeProcesses: Array<ProcessInvoker<TestActionContext>>,
        afterProcesses: Array<ProcessInvoker<TestActionContext>>,
        catchProcesses: Array<ProcessInvoker<TestActionContext>>
    ): DssActionInvoker<TestActionContext> =
        TestActionInvoker(
            channels,
            level,
            matchers,
            TestMethodInvoker(actionMethod, instanceGetter),
            beforeProcesses,
            afterProcesses,
            catchProcesses
        )

    override fun postLoad() {
        val actionList = ArrayList<ActionInfo<TestActionContext>>()
        root.controllers.forEach {
            it.actions.forEach {
                actionList.add(
                    ActionInfo(
                        it.actionClass,
                        it.actionMethod,
                        it.creator()
                    )
                )
            }
        }
        rootRouter = RootRouter(root.router, actionList)
    }


}
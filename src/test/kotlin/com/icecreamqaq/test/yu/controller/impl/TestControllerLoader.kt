package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.controller.dss.DssActionInvoker
import com.IceCreamQAQ.Yu.controller.dss.DssControllerLoader
import com.IceCreamQAQ.Yu.controller.dss.PathActionContext
import com.IceCreamQAQ.Yu.controller.dss.router.DssRouter
import com.IceCreamQAQ.Yu.controller.dss.router.DynamicRouter
import com.IceCreamQAQ.Yu.controller.dss.router.RouterMatcher
import com.IceCreamQAQ.Yu.di.YuContext
import java.lang.reflect.Method

class TestControllerLoader(
    context: YuContext
) : DssControllerLoader<PathActionContext, DssRouter<PathActionContext>, RootRouterProcessFlowInfo<PathActionContext, DssRouter<PathActionContext>>>(
    context
) {

    val root = RootRouterProcessFlowInfo<PathActionContext, DssRouter<PathActionContext>>(DssRouter(0))

    lateinit var rootRouter: RootRouter<DssRouter<PathActionContext>>

    override fun findRootRouter(name: String): RootRouterProcessFlowInfo<PathActionContext, DssRouter<PathActionContext>> =
        root


    override fun getSubStaticRouter(
        router: DssRouter<PathActionContext>,
        subPath: String
    ): DssRouter<PathActionContext> =
        router.staticSubrouter.getOrPut(subPath) {
            DssRouter(router.level + 1)
        }

    override fun getSubDynamicRouter(
        router: DssRouter<PathActionContext>,
        matcher: RouterMatcher<PathActionContext>
    ): DssRouter<PathActionContext> =
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
    ): ProcessInvoker<PathActionContext> =
        TestMethodInvoker(targetMethod, instanceGetter)

    override fun createCatchMethodInvoker(
        throwableType: Class<out Throwable>,
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<PathActionContext> =
        TestCatchMethodInvoker(targetMethod, instanceGetter, throwableType)

    override fun createActionInvoker(
        channels: List<String>,
        level: Int,
        matchers: List<RouterMatcher<PathActionContext>>,
        actionClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter,
        beforeProcesses: Array<ProcessInvoker<PathActionContext>>,
        afterProcesses: Array<ProcessInvoker<PathActionContext>>,
        catchProcesses: Array<ProcessInvoker<PathActionContext>>
    ): DssActionInvoker<PathActionContext> =
        DssActionInvoker(
            level,
            matchers,
            TestMethodInvoker(actionMethod, instanceGetter),
            beforeProcesses,
            afterProcesses,
            catchProcesses
        )

    override fun postLoad() {
        val actionList = ArrayList<ActionInfo>()
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
        println(rootRouter)
    }


}
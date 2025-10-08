package controller

import rain.api.di.DiContext
import rain.controller.ActionInfo
import rain.controller.ControllerInstanceGetter
import rain.controller.ProcessInvoker
import rain.controller.RootRouterProcessFlowInfo
import rain.controller.dss.DssActionInvoker
import rain.controller.dss.DssControllerLoader
import rain.controller.dss.router.DssRouter
import rain.controller.dss.router.DynamicRouter
import rain.controller.dss.router.RouterMatcher
import rain.controller.simple.SimpleCatchMethodInvoker
import rain.function.annotation
import java.lang.reflect.Method

class TestControllerLoader(
    context: DiContext
) : DssControllerLoader<
        TestActionContext,
        TestRouter,
        TestRootRouterProcessFlowInfo,
        TestActionInvoker
        >(
    context
) {

    val root = TestRootRouterProcessFlowInfo(TestRouter(0))

    lateinit var rootRouter: TestRootRouter

    override fun findRootRouter(name: String): TestRootRouterProcessFlowInfo =
        root


    override fun getSubStaticRouter(
        router: TestRouter,
        subPath: String
    ): TestRouter =
        router.static.getOrPut(subPath) {
            DssRouter(router.level + 1)
        }

    override fun getSubDynamicRouter(
        router: TestRouter,
        matcher: RouterMatcher<TestActionContext>
    ): TestRouter =
        let {
            router.dynamic.firstOrNull { it.matcher == matcher }
                ?: TestDynamicRouter(matcher, DssRouter(router.level + 1))
                    .apply { router.dynamic.add(this) }
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

    override fun putAction(
        router: TestRouter,
        channels: List<String>,
        actionInvoker: TestActionInvoker
    ) {
        var action = router.action
        if (action == null){
            action = TestActionChannelMapping()
            router.action = action
        }
        channels.forEach {
            when(it){
                "test1" -> action.test1 = actionInvoker
                "test2" -> action.test2 = actionInvoker
                "test3" -> action.test3 = actionInvoker
                "test4" -> action.test4 = actionInvoker
            }
        }
    }

    override fun createMethodInvoker(
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<TestActionContext> =
        TestMethodInvoker(targetMethod, instanceGetter).init()

    override fun createCatchMethodInvoker(
        throwableType: Class<out Throwable>,
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<TestActionContext> =
        SimpleCatchMethodInvoker(throwableType, TestMethodInvoker(targetMethod, instanceGetter).init())

    override fun createActionInvoker(
        channels: List<String>,
        actionClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter,
        beforeProcesses: Array<ProcessInvoker<TestActionContext>>,
        afterProcesses: Array<ProcessInvoker<TestActionContext>>,
        catchProcesses: Array<ProcessInvoker<TestActionContext>>
    ): TestActionInvoker =
        TestActionInvoker(
            TestMethodInvoker(actionMethod, instanceGetter).init(),
            beforeProcesses,
            afterProcesses,
            catchProcesses
        )

    override fun postLoad() {
        rootRouter = buildRootInfo(root)
    }


}
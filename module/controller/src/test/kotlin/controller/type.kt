package controller

import rain.controller.ControllerProcessFlowInfo
import rain.controller.RootRouter
import rain.controller.RootRouterProcessFlowInfo
import rain.controller.dss.router.DssRouter
import rain.controller.dss.router.DynamicRouter

class TestRouter(
    level: Int
) : DssRouter<TestActionContext, TestActionChannelMapping, TestRouter, TestDynamicRouter>(level)
typealias TestDynamicRouter = DynamicRouter<TestActionContext, TestActionChannelMapping, TestRouter>

typealias TestRootRouterProcessFlowInfo = RootRouterProcessFlowInfo<TestActionContext, TestRouter, TestActionInvoker>
typealias TestRootRouter = RootRouter<TestActionContext, TestRouter, TestActionInvoker>

typealias TestControllerProcessFlowInfo = ControllerProcessFlowInfo<TestActionContext, TestRouter, TestActionInvoker>
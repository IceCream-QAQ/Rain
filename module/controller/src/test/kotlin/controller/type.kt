package controller

import rain.controller.RootRouter
import rain.controller.RootRouterProcessFlowInfo
import rain.controller.dss.router.DssRouter
import rain.controller.dss.router.DynamicRouter

typealias TestRouter = DssRouter<TestActionContext, TestActionChannelMapping>
typealias TestDynamicRouter = DynamicRouter<TestActionContext, TestActionChannelMapping>
typealias TestRootRouterProcessFlowInfo = RootRouterProcessFlowInfo<TestActionContext, TestRouter, TestActionInvoker>
typealias TestRootRouter = RootRouter<TestActionContext, TestRouter, TestActionInvoker>
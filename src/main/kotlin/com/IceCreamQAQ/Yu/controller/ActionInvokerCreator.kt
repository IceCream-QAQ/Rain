package com.IceCreamQAQ.Yu.controller

fun interface ActionInvokerCreator<CTX : ActionContext, ROT : Router> {

    operator fun invoke(
        rootInfo: RootRouterProcessFlowInfo<CTX,ROT>,
        controllerInfo: ControllerProcessFlowInfo<CTX,ROT>,
        actionInfo:ActionProcessFlowInfo<CTX,ROT>
    ): ActionInvoker<CTX>

}
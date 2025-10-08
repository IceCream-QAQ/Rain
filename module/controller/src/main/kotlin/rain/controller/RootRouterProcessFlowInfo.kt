package rain.controller

open class RootRouterProcessFlowInfo<CTX : ActionContext, ROT : Router, AI: ActionInvoker<CTX>>(
    open val router: ROT,
    open val controllers: MutableList<ControllerProcessFlowInfo<CTX, ROT, AI>> = arrayListOf(),
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses)
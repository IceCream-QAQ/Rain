package rain.controller

open class ControllerProcessFlowInfo<CTX : ActionContext, ROT : Router, AI : ActionInvoker<CTX>>(
    open val controllerClass: Class<*>,
    open val controllerInstance: Any,
    open val controllerChannels: List<String>,
    open val controllerRouter: ROT,
    open val actions: MutableList<ActionCreator<CTX, AI>> = arrayListOf(),
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses)
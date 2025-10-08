package rain.controller

fun interface ActionInvokerCreator<CTX : ActionContext, AI : ActionInvoker<CTX>> {

    operator fun invoke(
        beforeProcesses: Array<ProcessInvoker<CTX>>,
        afterProcesses: Array<ProcessInvoker<CTX>>,
        catchProcesses: Array<ProcessInvoker<CTX>>,
    ): AI

}
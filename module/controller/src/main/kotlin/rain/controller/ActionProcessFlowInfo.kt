package rain.controller

import java.lang.reflect.Method

open class ActionProcessFlowInfo<CTX : ActionContext>(
    val actionClass: Class<*>,
    val actionMethod: Method,
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses) {
    open lateinit var creator: ActionInvokerCreator<CTX>
}
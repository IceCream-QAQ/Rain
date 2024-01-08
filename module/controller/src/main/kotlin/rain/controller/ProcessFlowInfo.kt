package rain.controller

open class ProcessFlowInfo<T : ActionContext>(
    open val beforeProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    open val afterProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    open val catchProcesses: MutableList<ProcessInfo<T>> = arrayListOf()
)
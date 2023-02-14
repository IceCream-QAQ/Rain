package com.IceCreamQAQ.Yu.controller

open class ActionProcessFlowInfo<CTX : ActionContext>(
    open val invoker: ActionInvoker<CTX>,
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses)
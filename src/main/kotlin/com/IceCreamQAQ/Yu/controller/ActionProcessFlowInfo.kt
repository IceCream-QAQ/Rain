package com.IceCreamQAQ.Yu.controller

open class ActionProcessFlowInfo<CTX : ActionContext, ROT : Router>(
    open val invoker: ActionInvokerCreator<CTX,ROT>,
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses)
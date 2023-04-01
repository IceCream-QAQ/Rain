package com.IceCreamQAQ.Yu.controller

open class ControllerProcessFlowInfo<CTX : ActionContext, ROT : Router>(
    open val controllerClass: Class<*>,
    open val controllerChannels: List<String>,
    open val controllerRouter: ROT,
    open val actions: MutableList<ActionProcessFlowInfo<CTX>> = arrayListOf(),
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses)
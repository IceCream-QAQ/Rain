package com.IceCreamQAQ.Yu.controller

open class ControllerProcessFlowInfo<CTX : ActionContext>(
    val controllerRouter: Router,
    val actions: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    beforeProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<CTX>> = arrayListOf()
) : ProcessFlowInfo<CTX>(beforeProcesses, afterProcesses, catchProcesses)
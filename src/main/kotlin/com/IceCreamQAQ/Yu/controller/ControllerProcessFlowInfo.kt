package com.IceCreamQAQ.Yu.controller

open class ControllerProcessFlowInfo<T : ActionContext>(
    val controllerRouter: Router,
    val actions: MutableList<ProcessInvoker<T>> = arrayListOf(),
    beforeProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<T>> = arrayListOf()
) : ProcessFlowInfo<T>(beforeProcesses, afterProcesses, catchProcesses)
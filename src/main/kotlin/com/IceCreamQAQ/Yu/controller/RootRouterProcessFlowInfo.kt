package com.IceCreamQAQ.Yu.controller

open class RootRouterProcessFlowInfo<T : ActionContext>(
    val controllers: MutableList<ControllerProcessFlowInfo<T>> = arrayListOf(),
    val channels: Map<String, Router> = hashMapOf(),
    beforeProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<T>> = arrayListOf()
) : ProcessFlowInfo<T>(beforeProcesses, afterProcesses, catchProcesses)
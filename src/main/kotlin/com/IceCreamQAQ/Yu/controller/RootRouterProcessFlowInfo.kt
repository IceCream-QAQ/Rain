package com.IceCreamQAQ.Yu.controller

open class RootRouterProcessFlowInfo<T : ActionContext, R : Router>(
    open val controllers: MutableList<ControllerProcessFlowInfo<T,R>> = arrayListOf(),
    open val channels: MutableMap<String, R> = hashMapOf(),
    beforeProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    afterProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    catchProcesses: MutableList<ProcessInfo<T>> = arrayListOf()
) : ProcessFlowInfo<T>(beforeProcesses, afterProcesses, catchProcesses)
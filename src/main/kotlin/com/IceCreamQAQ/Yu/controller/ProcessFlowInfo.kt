package com.IceCreamQAQ.Yu.controller

open class ProcessFlowInfo<T : ActionContext>(
    val beforeProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    val afterProcesses: MutableList<ProcessInfo<T>> = arrayListOf(),
    val catchProcesses: MutableList<ProcessInfo<T>> = arrayListOf()
)
package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
import java.lang.reflect.Method

data class DoMethod<T : Annotation, I>(val annotation: T, val invoker: I)
data class DoCatch(val catch: Catch, val invoker: CatchInvoker)
data class ActionMap(
    val action: Action,
    val method: Method,
    val priority: Int = action.loadPriority
) : Comparable<ActionMap> {
    override fun compareTo(other: ActionMap): Int = this.priority - other.priority
}

data class InterceptorInfo(
    val befores: MutableList<DoMethod<Before, MethodInvoker>>,
    val afters: MutableList<DoMethod<After, MethodInvoker>>,
    val catchs: MutableList<DoMethod<Catch, CatchInvoker>>
)
package com.IceCreamQAQ.Yu.controller.simple

import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.controller.ActionInvoker
import com.IceCreamQAQ.Yu.controller.ProcessInvoker
import com.IceCreamQAQ.Yu.controller.special.ActionResult
import com.IceCreamQAQ.Yu.controller.special.DoNone
import com.IceCreamQAQ.Yu.controller.special.SkipMe
import com.IceCreamQAQ.Yu.toLowerCaseFirstOne

open class SimpleActionInvoker<CTX : ActionContext>(
    val action: ProcessInvoker<CTX>,
    val beforeProcesses: Array<ProcessInvoker<CTX>>,
    val aftersProcesses: Array<ProcessInvoker<CTX>>,
    val catchsProcesses: Array<ProcessInvoker<CTX>>
) : ActionInvoker<CTX> {

    override fun invoke(context: CTX): Boolean {
        kotlin.runCatching {
            if (beforeProcesses.any { onProcessResult(context, it(context)) }) return@runCatching
            if (onActionResult(context, action(context))) return@runCatching
            if (aftersProcesses.any { onProcessResult(context, it(context)) }) return@runCatching
        }.getOrElse {
            if (it !is ActionResult) {
                context.runtimeError = it
                catchsProcesses.any { onProcessResult(context, it(context)) }
            } else onProcessResult(context, it.result)
        }

        if (checkActionResult(context)) return true
        return false
    }

    open fun checkResult(context: CTX, result: Any): Boolean {
        if (result is DoNone || result is SkipMe) {
            context.result = result
            return true
        }
        return false
    }

    open fun onProcessResult(context: CTX, result: Any?): Boolean {
        if (result == null) return false
        if (checkResult(context, result)) return true

        context[result::class.java.simpleName.toLowerCaseFirstOne()] = result
        return false
    }

    open fun onActionResult(context: CTX, result: Any?): Boolean {
        if (result == null) return false
        if (checkResult(context, result)) return true

        context.result = result
        return false
    }

    open fun checkActionResult(context: CTX): Boolean {
        if (context.result is SkipMe) return false
        return true
    }
}
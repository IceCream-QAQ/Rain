package rain.controller.simple

import rain.controller.special.ActionResult
import rain.controller.ActionContext
import rain.controller.ActionInvoker
import rain.controller.ProcessInvoker
import rain.controller.special.DoNone
import rain.controller.special.SkipMe
import rain.function.toLowerCaseFirstOne

abstract class SimpleActionInvoker<CTX : ActionContext>(
    open val action: ProcessInvoker<CTX>,
    open val beforeProcesses: Array<ProcessInvoker<CTX>>,
    open val aftersProcesses: Array<ProcessInvoker<CTX>>,
    open val catchsProcesses: Array<ProcessInvoker<CTX>>
) : ActionInvoker<CTX> {

    override suspend fun invoke(context: CTX): Boolean {
        if (!checkChannel(context)) return false
        kotlin.runCatching {
            if (beforeProcesses.any { onProcessResult(context, it(context)) }) return@runCatching
            if (onActionResult(context, action(context))) return@runCatching
            if (aftersProcesses.any { onProcessResult(context, it(context)) }) return@runCatching
        }.getOrElse {
            if (it !is ActionResult) {
                context.runtimeError = it
                kotlin.runCatching { catchsProcesses.any { onProcessResult(context, it(context)) } }
                    .getOrElse { exception ->
                        if (exception is ActionResult) {
                            context.runtimeError = null
                            onActionResult(context, exception.result)
                        } else throw it
                    }
            } else {
                context.runtimeError = null
                onActionResult(context, it.result)
            }
        }

        return checkActionResult(context)
    }

    abstract suspend fun checkChannel(context: CTX): Boolean

    open suspend fun checkResult(context: CTX, result: Any): Boolean {
        if (result is DoNone || result is SkipMe) {
            context.result = result
            return true
        }
        return false
    }

    open suspend fun onProcessResult(context: CTX, result: Any?): Boolean {
        if (result == null) return false
        if (checkResult(context, result)) return true

        context[result::class.java.simpleName.toLowerCaseFirstOne()] = result
        return false
    }

    open suspend fun onActionResult(context: CTX, result: Any?): Boolean {
        if (result == null) return false
        if (checkResult(context, result)) return true

        context.result = result
        return false
    }

    open suspend fun checkActionResult(context: CTX): Boolean {
        if (context.result is SkipMe) return false
        return true
    }
}
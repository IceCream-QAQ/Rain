package rain.job

import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend

fun interface CronInvoker {
    suspend operator fun invoke()

    companion object {
        fun reflect(instance: Any, method: Method) = CronInvoker { method.invoke(instance) }
        fun kFun(instance: Any, method: KFunction<*>) = CronInvoker { method.call(instance) }
        fun suspendFun(instance: Any, method: KFunction<*>) = CronInvoker { method.callSuspend(instance) }
        fun dsl(function: suspend () -> Unit) = CronInvoker(function)
        fun runnable(runnable: Runnable) = CronInvoker { runnable.run() }
    }
}
package rain.controller.simple

import rain.controller.ActionContext
import rain.controller.ProcessInvoker

class SimpleCatchMethodInvoker<CTX : ActionContext>(
    val errorType: Class<out Throwable>,
    val invoker: ProcessInvoker<CTX>
) : ProcessInvoker<CTX> {

    override suspend fun invoke(context: CTX): Any? {
        if (context.runtimeError == null || !errorType.isInstance(context.runtimeError)) return null
        return invoker.invoke(context)
    }

}
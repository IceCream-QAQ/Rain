package rain.controller.dss

import rain.controller.ProcessInvoker
import rain.controller.dss.router.RouterMatcher
import rain.controller.simple.SimpleActionInvoker

abstract class DssActionInvoker<CTX : PathActionContext>(
    val level: Int,
    val matchers: List<RouterMatcher<CTX>>,
    action: ProcessInvoker<CTX>,
    beforeProcesses: Array<ProcessInvoker<CTX>>,
    aftersProcesses: Array<ProcessInvoker<CTX>>,
    catchsProcesses: Array<ProcessInvoker<CTX>>
) : SimpleActionInvoker<CTX>(action, beforeProcesses, aftersProcesses, catchsProcesses) {

    override suspend fun invoke(context: CTX): Boolean {
        matchers.forEachIndexed { i, matcher ->
            if (!matcher(runCatching { context.path[i + level] }.getOrNull(), context)) return false
        }
        return super.invoke(context)
    }

}
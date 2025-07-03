package rain.controller.dss.router

import rain.controller.ActionInvoker
import rain.controller.Router
import rain.controller.dss.PathActionContext

/** 路由架构描述
 * 路由是一个多级结构。
 * 路由分为两个部分，子集路由 和 当前 Action。
 * 子集部分又分为 静态子集 和 动态子集。
 *
 * 路由仅处理自己路由匹配完成的 Action。
 * 路由匹配完全交由路由实现，而不是 Action 实现。
 * Action 与路由完全无关。
 */
open class DssRouter<CTX : PathActionContext, AI : ActionInvoker<CTX>>(
    val level: Int
) : Router {

    val static = HashMap<String, DssRouter<CTX, AI>>()
    val dynamic = ArrayList<DynamicRouter<CTX, AI>>()

    var action: AI? = null

    suspend operator fun invoke(context: CTX): Boolean {
        if (context.path.size > level) {
            val path = context.path[level]
            if (static[path]?.invoke(context) == true) return true
            if (dynamic.any { it.matcher(path, context) && it.router(context) }) return true
        }

        return action?.invoke(context) ?: false
    }

}
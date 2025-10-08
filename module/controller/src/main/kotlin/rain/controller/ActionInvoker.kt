package rain.controller

import java.lang.reflect.Method

interface ActionInvoker<CTX : ActionContext> {

    val actionClass: Class<*>?
    val actionMethod: Method?

    suspend operator fun invoke(context: CTX): Boolean

}
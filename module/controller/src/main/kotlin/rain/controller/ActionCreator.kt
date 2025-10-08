package rain.controller

import java.lang.reflect.Method

open class ActionCreator<CTX : ActionContext, AI : ActionInvoker<CTX>>(
    open val controllerClass: Class<*>,
    open val actionMethod: Method,
    open val creator: ActionInvokerCreator<CTX, AI>
){
    override fun toString(): String {
        return "ActionCreator(controllerClass=$controllerClass, actionMethod=$actionMethod, creator=$creator)"
    }
}
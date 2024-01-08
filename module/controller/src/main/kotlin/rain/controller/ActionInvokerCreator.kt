package rain.controller

fun interface ActionInvokerCreator<CTX : ActionContext> {

    operator fun invoke(): ActionInvoker<CTX>

}
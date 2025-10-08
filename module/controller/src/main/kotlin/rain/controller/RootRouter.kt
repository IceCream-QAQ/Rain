package rain.controller

open class RootRouter<CTX : ActionContext, ROT : Router, AI: ActionInvoker<CTX>>(
    val router: ROT,
    val actions: List<AI>
)
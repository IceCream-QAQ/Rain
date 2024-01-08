package rain.controller

open class RootRouter<CTX: ActionContext,ROT : Router>(
    val router: ROT,
    val actions: List<ActionInfo<CTX>>
)
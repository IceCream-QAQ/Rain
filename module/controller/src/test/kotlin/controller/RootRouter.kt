package controller

import rain.controller.ActionContext
import rain.controller.ActionInfo
import rain.controller.Router

open class RootRouter<CTX: ActionContext,ROT : Router>(
    val router: ROT,
    val actions: List<ActionInfo<CTX>>
)
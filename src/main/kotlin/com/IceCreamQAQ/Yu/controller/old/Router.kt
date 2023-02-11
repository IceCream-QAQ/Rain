package com.IceCreamQAQ.Yu.controller.old

import com.IceCreamQAQ.Yu.annotation.AutoBind

@AutoBind
interface Router {

    fun init(rootRouter: RootRouter)

    suspend operator fun invoke(path: String, context: ActionContext): Boolean
}






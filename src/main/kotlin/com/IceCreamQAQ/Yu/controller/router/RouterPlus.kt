package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.controller.ActionContext

@Deprecated("已经弃用")
@AutoBind
interface RouterPlus {

    @Throws(Exception::class)
    fun invoke(path: String, context: ActionContext):Boolean

//    fun putInvoker()

}
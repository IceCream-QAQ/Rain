package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.toLowerCaseFirstOne
import java.lang.reflect.Method
import java.util.regex.Pattern
import javax.inject.Named

@AutoBind
interface Router {

    fun init(rootRouter: RootRouter)

    operator fun invoke(path: String, context: ActionContext): Boolean
}






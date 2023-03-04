package com.IceCreamQAQ.Yu.controller

import java.lang.reflect.Method

open class ActionInfo<CTX : ActionContext>(
    val actionClass: Class<*>,
    val actionMethod: Method,
    val actionInvoker: ActionInvoker<CTX>
)
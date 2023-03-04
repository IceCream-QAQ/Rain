package com.IceCreamQAQ.Yu.controller

import java.lang.reflect.Method

data class ActionInfo(
    val actionClass: Class<*>,
    val actionMethod: Method,
    val actionInvoker: ActionInvoker<out ActionContext>
)
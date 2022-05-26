package com.IceCreamQAQ.Yu.util

import kotlin.RuntimeException

class YuParaValueException(message: String) : RuntimeException(message)
class RainEventException(message: String = "", cause: Exception) : RuntimeException(cause)
class ControllerInvokeException(
    private val controllerName: String,
    private val actionName: String,
    private val actionFullName: String,
    private val errorMethodFullName: String,
    private val errorType: String,
    override val cause: Throwable
) : RuntimeException() {
    override val message: String
        get() = """
            在执行 Controller 调用时遇到异常。
            路由位置: 
                Controller: $controllerName
                Action: $actionName
                ActionMethod: $actionFullName
            异常类型: $errorType 执行时产生异常。
                异常方法: $errorMethodFullName
                异常信息: ${cause.message}
        """.trimIndent()
}

public fun error(message: Any, cause: Throwable): Nothing =
    throw IllegalStateException(message.toString(), cause)

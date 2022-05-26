package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.fullName
import java.lang.reflect.Method
import javax.inject.Named


interface MethodInvoker {
    val methodName: String
    val fullName: String
    fun init()
    @Throws(Exception::class)
    suspend fun invoke(context: ActionContext): Any?
}

interface CatchInvoker {
    val methodName: String
    val fullName: String
    fun init()
    @Throws(Exception::class)
    suspend fun invoke(context: ActionContext, error: Throwable): Any?
}
package com.IceCreamQAQ.Yu.controller

interface ActionContext {

//    val method: String

    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any)

    suspend fun onError(e: Throwable): Throwable?
    suspend fun onSuccess(result: Any?): Any?

}


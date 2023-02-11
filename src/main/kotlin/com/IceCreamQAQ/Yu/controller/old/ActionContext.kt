package com.IceCreamQAQ.Yu.controller.old

interface ActionContext {

    var path: Array<String>

    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any)

    suspend fun onError(e: Throwable): Throwable?
    suspend fun onSuccess(result: Any?): Any?

}


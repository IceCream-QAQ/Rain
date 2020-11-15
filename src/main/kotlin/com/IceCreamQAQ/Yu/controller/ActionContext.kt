package com.IceCreamQAQ.Yu.controller

interface ActionContext {

    var path: Array<String>

    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any)

    fun onError(e: Throwable): Throwable?
    fun onSuccess(result: Any?): Any?

}


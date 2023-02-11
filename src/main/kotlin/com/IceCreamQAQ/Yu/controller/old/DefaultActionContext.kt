package com.IceCreamQAQ.Yu.controller.old

class DefaultActionContext : ActionContext {

    override lateinit var path: Array<String>
    var saves = HashMap<String, Any>()

    var result: Any? = null

    override fun get(name: String): Any? = saves[name]

    override fun set(name: String, obj: Any) {
        saves[name] = obj
    }

    override suspend fun onError(e: Throwable): Throwable = e

    override suspend fun onSuccess(result: Any?): Any? {
        this.result = result
        return null
    }
}
package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.entity.Result

interface NewActionContext {

    var path: Array<String>

    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any)

    fun onError(e: Throwable): Throwable?
    fun onSuccess(result: Any?): Any?

}

class NewActionContextImpl : NewActionContext {

    override lateinit var path: Array<String>
    var saves = HashMap<String, Any>()

    var result: Any? = null

    override fun get(name: String): Any? = saves[name]

    override fun set(name: String, obj: Any) {
        saves[name] = obj
    }

    override fun onError(e: Throwable): Throwable = e

    override fun onSuccess(result: Any?): Any? {
        this.result = result
        return null
    }
}
package com.IceCreamQAQ.Yu.controller.dss

import com.IceCreamQAQ.Yu.controller.ActionContext

open class PathActionContext(val path: Array<String>) : ActionContext {

    val saves = HashMap<String, Any>()
    override var runtimeError: Throwable? = null
    override var result: Any? = null

    override fun get(name: String) = saves[name]

    override fun set(name: String, obj: Any) {
        saves[name] = obj
    }

    override fun remove(name: String) = saves.remove(name)
}
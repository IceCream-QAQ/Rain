package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.entity.Result

class DefaultActionContext :ActionContext {

    private lateinit var path:Array<String>
    private val saves = HashMap<String,Any>()
    private var result:Result? = null

    init {
        this["actionContext"] = this
    }

    override fun getPath(): Array<String> {
        return path
    }

    fun setPath(path: Array<String>){
        this.path=path
    }

    override fun get(name: String): Any? {
        return saves[name]
    }

    override fun set(name: String, obj: Any) {
        saves[name] = obj
    }


    override fun setResult(result: Result) {
        this.result=result
    }

    override fun buildResult(obj: Any): Result {
        return Result()
    }
}
package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.entity.Result

@Deprecated("已经弃用")
class DefaultActionContext :ActionContext {

    override lateinit var path: Array<String>
    override var result: Result? = null

    //    private lateinit var path:Array<String>
    private val saves = HashMap<String,Any>()
//    private var result:Result? = null

    init {
        this["actionContext"] = this
    }


    override fun get(name: String): Any? {
        return saves[name]
    }

    override fun set(name: String, obj: Any) {
        saves[name] = obj
    }

    override fun buildResult(obj: Any): Result {
        return TestResult(obj)
    }

    @Deprecated("已经弃用")
    data class TestResult(val obj:Any):Result()
}

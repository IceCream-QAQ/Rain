package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.entity.Result

interface ActionContext {

    var path:Array<String>
    var result:Result?
//    fun getPath(): Array<String>

    operator fun get(name:String):Any?
    operator fun set(name: String,obj: Any)

//    fun setResult(result: Result)

    fun buildResult(obj:Any):Result?

}
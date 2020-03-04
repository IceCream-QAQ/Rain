package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.entity.Result

interface ActionContext {

    fun getPath(): Array<String>

    fun saveSomething(obj: Any, name: String)

    fun setResult(result: Result)

}
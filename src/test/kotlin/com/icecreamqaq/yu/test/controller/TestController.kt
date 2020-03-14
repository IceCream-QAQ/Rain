package com.icecreamqaq.yu.test.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.PathVar
import com.icecreamqaq.yu.test.annotation.CController
import javax.inject.Named

@CController
class TestController {

    @Before
    fun testBefore():String{
        return "Test Before"
    }

    @Action("test")
    fun testAction(@Named("string")@PathVar(value = 2,type = PathVar.Type.number) before:String){
        println("before = $before")
        println("Test Action")
    }

}
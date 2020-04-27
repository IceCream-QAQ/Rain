package com.icecreamqaq.yu.test.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.DefaultController
import com.IceCreamQAQ.Yu.annotation.PathVar
import com.icecreamqaq.yu.test.annotation.CController
import javax.inject.Named

@DefaultController
class TestController {

    @Before
    fun testBefore():String{
        return "Test Before"
    }

    @Action("test")
    fun testAction(@PathVar(value = 2, type = PathVar.Type.number) aaa: String, bbb: String, @Named("111") ccc: String) {
        println("before = $aaa")
        println("Test Action")
    }

}
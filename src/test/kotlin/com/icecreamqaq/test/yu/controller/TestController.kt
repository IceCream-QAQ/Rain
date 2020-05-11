package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.DefaultController
import com.IceCreamQAQ.Yu.annotation.PathVar
import com.icecreamqaq.test.yu.annotation.CController
import javax.inject.Named

@DefaultController
class TestController {

    @Before
    fun testBefore(): String {
        return "Test Before"
    }

    @Action("t1")
    fun t1() = ""

    @Action("test")
    fun testAction(aaa: String, bbb: String, ccc: String) {
        val ddd = "123412"
        println("before = $aaa")
        println("Test Action")
    }

}
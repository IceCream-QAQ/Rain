package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.DefaultController
import com.IceCreamQAQ.Yu.cache.EhcacheHelp
import com.icecreamqaq.test.yu.util.TestUtil
import javax.inject.Inject
import javax.inject.Named

@DefaultController
class TestController {

    @Inject
    @field:Named("testCache")
    private lateinit var c: EhcacheHelp<String>

    @Before
    fun testBefore(): String {
        return "Test Before"
    }

    @Action("t1")
    fun t1() = c["aaa"]

    @Action("t2")
    fun t2() {
        c["aaa"] = "bbb"
    }

    @Action("test")
    fun testAction(aaa: String, bbb: String, ccc: String) {
        val ddd = "123412"
        println("before = $aaa")
        println("Test Action")
    }

}
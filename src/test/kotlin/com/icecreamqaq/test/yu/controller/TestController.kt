package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.*
import com.IceCreamQAQ.Yu.cache.EhcacheHelp
import com.IceCreamQAQ.Yu.controller.ActionContext
import com.icecreamqaq.test.yu.util.TestUtil
import javax.inject.Inject
import javax.inject.Named

@DefaultController
class TestController {

    @Inject
    @field:Named("testCache")
    private lateinit var c: EhcacheHelp<String>

    @Config("conf.test")
    @Default("123456")
    private lateinit var conf: String

    @Before
    fun testBefore(): String {
        return "Test Before"
    }

    @Action("t1")
    fun t1() = c["aaa"]

    @Action("t2")
    fun t2() {
        c["aaa"] = conf
    }

    @Action("{t3.*}")
    fun t3(actionContext: ActionContext) {
        println(actionContext.path[0])
    }

    @Action("test")
    fun testAction(aaa: String, bbb: String, ccc: String) {
        val ddd = "123412"
        println("before = $aaa")
        println("Test Action")
    }

}
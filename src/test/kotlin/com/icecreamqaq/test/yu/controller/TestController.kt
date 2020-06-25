package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.*
import com.IceCreamQAQ.Yu.cache.EhcacheHelp
import com.IceCreamQAQ.Yu.controller.ActionContext
import com.icecreamqaq.test.yu.util.TestUtil
import javax.inject.Inject
import javax.inject.Named
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@NotSearch
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
    @Synonym(["{t4.*}"])
    fun t3(actionContext: ActionContext) {
        println(actionContext.path[0])
    }

    @Action("test")
    fun testAction(@NotNull(message = "请输入正确参数！") aaa: String,
                   @Min(message = "bbb不能低于7！", value = 7) @Max(value = 12, message = "bbb不能高于12！") bbb: Int,
                   ccc: String) {
        val ddd = "123412"
        println("before = $aaa")
        println("Test Action")
    }

}
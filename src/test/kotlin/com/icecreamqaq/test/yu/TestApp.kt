package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.DefaultApp
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.controller.DefaultActionContext
import com.IceCreamQAQ.Yu.controller.NewActionContextImpl
import com.IceCreamQAQ.Yu.controller.router.NewRouter
import com.IceCreamQAQ.Yu.controller.router.RouterPlus
import com.IceCreamQAQ.Yu.di.YuContext
import com.icecreamqaq.test.yu.util.TestUtil
import java.util.HashMap
import javax.inject.Inject

@NotSearch
class TestApp : DefaultApp() {

    @Inject
    lateinit var context: YuContext

    fun test() {
        val test = this.context.getBean(TestUtil::class.java, "123")
        println(test)

//        val router = context.getBean(RouterPlus::class.java, "default")!!

//        val ac = DefaultActionContext()

        val paths = arrayOf("11", "menu", "123321")
//        ac.path = paths
//
//        router.invoke(paths[0], ac)
//
//        paths[0] = "t1"
//        router.invoke(paths[0], ac)
//
//        paths[0] = "t3t3t3"
//        router.invoke(paths[0], ac)
//        paths[0] = "t4t4t4"
//        router.invoke(paths[0], ac)
//
//        println("result: ${(ac.result as TestResult).obj}")

        val newRouter = context.getBean(NewRouter::class.java, "default")!!

        val nac = NewActionContextImpl()
        nac.path = paths
        nac.saves = HashMap()

        paths[0] = "menu"
        newRouter.invoke(paths[0], nac)
        println("menu: " + nac.result)
        nac.result = null
        nac.saves = HashMap()

        paths[0] = "m111b222"
        newRouter.invoke(paths[0], nac)
        println("m111b222: " + nac.result)
        nac.result = null
        nac.saves = HashMap()

        paths[0] = "c333b444"
        newRouter.invoke(paths[0], nac)
        println("c333b444: " + nac.result)
        nac.result = null
        nac.saves = HashMap()

        paths[0] = "11"
        newRouter.invoke(paths[0], nac)
        println("11: " + nac.result)
        nac.result = null
        nac.saves = HashMap()

        paths[1] = "menu"
        newRouter.invoke(paths[0], nac)
        println("menu: ${nac.result}")
        nac.result = null
        nac.saves = HashMap()

        paths[1] = "123123"
        paths[2] = "menu"
        newRouter.invoke(paths[0], nac)
        println("menu2: ${nac.result}")
        nac.result = null
        nac.saves = HashMap()

        paths[0] = "BV1vh411o7p3"
        newRouter.invoke(paths[0], nac)
        println("BV: " + nac.result)
        nac.result = null
        nac.saves = HashMap()

        paths[0] = "coc0001"
        newRouter.invoke(paths[0], nac)
        println("coc: " + nac.result)
        nac.result = null
        nac.saves = HashMap()
    }
}
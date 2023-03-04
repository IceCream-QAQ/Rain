package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.hook.HookContext
//import com.IceCreamQAQ.Yu.controller.RoutersMap
import com.IceCreamQAQ.Yu.hook.HookRunnable
import kotlinx.coroutines.runBlocking
import kotlin.collections.HashMap

@NotSearch
class TestApp {

//    override fun start() {
//        loader.loaderRewrite[EventListenerLoader::class.java] = TestRewriteEventListenerLoader::class.java
//        super.start()
//    }

    private lateinit var context:YuContext

    fun test() {
//        val test = this.context.getBean(TestUtil::class.java, "123")
//        println(test)

//        val router = context.getBean(::class.java, "default")!!
//
//        val ac = DefaultActionContextImpl()
//
        runBlocking {
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
//        val rm = context.getBean(RoutersMap::class.java, "default")!!

        }
    }
}

class HookStartEvent : HookRunnable {

    override fun preRun(method: HookContext): Boolean {
        println("Hook StartEvent PerRun")
        return false
    }
}
package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.DefaultApp
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.controller.DefaultActionContext
import com.IceCreamQAQ.Yu.controller.TestResult
import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.RouterPlus
import com.IceCreamQAQ.Yu.di.ConfigManager
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.job.JobManager_
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.AppLoader_
import com.icecreamqaq.test.yu.util.TestAbs
import com.icecreamqaq.test.yu.util.TestInf
import com.icecreamqaq.test.yu.util.TestUtil
import javax.inject.Inject

@NotSearch
class TestApp : DefaultApp() {

    @Inject
    lateinit var context: YuContext

    fun test() {
        val test = this.context.getBean(TestUtil::class.java, "123")
        println(test)

        val router = context.getBean(RouterPlus::class.java, "default")!!

        val ac = DefaultActionContext()

        val paths = arrayOf("t2", "ttt")
        ac.path = paths

        router.invoke(paths[0], ac)

        paths[0] = "t1"
        router.invoke(paths[0], ac)

        paths[0] = "t3t3t3"
        router.invoke(paths[0], ac)

        println("result: ${(ac.result as TestResult).obj}")
    }
}
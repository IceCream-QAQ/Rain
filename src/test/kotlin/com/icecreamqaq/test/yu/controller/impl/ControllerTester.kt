package com.icecreamqaq.test.yu.controller.impl

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.di.kotlin.inject
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.util.sout

@EventListener
class ControllerTester {

    val controllerLoader by inject<TestControllerLoader>()

    @Event
    fun onStart(e: AppStartEvent) {
        println("---------------- Controller Test ----------------")

        val router = controllerLoader.rootRouter.router

        fun test(channel: String, vararg paths: String) {
            TestActionContext(channel, paths as Array<String>)
                .apply {
                    router(this)
                        .let {
                            println("Path: ${StringBuilder().apply { path.forEach { append("/").append(it) } }}, Channel: $channel, Flag: $it, Result: $result.")
                        }
                }
        }

        println("---- 1234")
        test("test1", "testAction1")
        test("test2", "testAction2")
        test("test3", "testAction3")
        test("test4", "testAction4")

        println("---- TestAction")
        test("test1", "testAction")
        test("test2", "testAction")
        test("test3", "testAction")
        test("test4", "testAction")

        println("---- 复杂部分")
        test("test4", "testPath", "testAction4")
        test("test4", "testPathVar", "HelloPathVar")

        println("-------------- Controller Test End --------------")
    }
}
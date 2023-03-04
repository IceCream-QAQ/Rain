package com.icecreamqaq.test.yu.controller.test

import com.icecreamqaq.test.yu.controller.impl.*

@TestController
class NoPathController {

    @TestAction("testAction")
    fun testAction(): String {
        return "testAction"
    }

    @TestAction1("testAction1")
    fun testAction1(): String {
        return "testAction1"
    }
    @TestAction2("testAction2")
    fun testAction2(): String {
        return "testAction2"
    }
    @TestAction3("testAction3")
    fun testAction3(): String {
        return "testAction3"
    }
    @TestAction4("testAction4")
    fun testAction4(): String {
        return "testAction4"
    }

}
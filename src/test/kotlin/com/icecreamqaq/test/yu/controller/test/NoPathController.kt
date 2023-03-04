package com.icecreamqaq.test.yu.controller.test

import com.icecreamqaq.test.yu.controller.impl.TestAction
import com.icecreamqaq.test.yu.controller.impl.TestController

@TestController
class NoPathController {

    @TestAction("testAction")
    fun testAction(): String {
        return ""
    }

}
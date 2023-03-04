package com.icecreamqaq.test.yu.controller.test

import com.IceCreamQAQ.Yu.annotation.Path
import com.icecreamqaq.test.yu.controller.impl.TestAction4
import com.icecreamqaq.test.yu.controller.impl.TestController

@Path("testPath")
@TestController
class PathController {

    @TestAction4("testAction4")
    fun testAction4() = "Hello World!"

}
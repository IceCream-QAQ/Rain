package application

import controller.TestAction4
import controller.TestController
import rain.controller.annotation.Path

@Path("testPath")
@TestController
class PathController {

    @TestAction4("testAction4")
    fun testAction4() = "Hello World!"

}
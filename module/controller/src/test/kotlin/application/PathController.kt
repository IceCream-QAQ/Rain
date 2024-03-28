package application

import controller.TestAction4
import controller.TestController
import rain.controller.annotation.Before
import rain.controller.annotation.Global
import rain.controller.annotation.Path

@Path("testPath")
@TestController
class PathController {

    @Before
    @Global
    fun testGlobalBefore(){
        println("testGlobalBefore")
    }

    @TestAction4("testAction4")
    fun testAction4() = "Hello World!"

}
package application

import controller.*
import rain.controller.annotation.Before


@TestController
class NoPathController {

    @Before
    fun testBefore(){
        println("testBefore")
    }

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
    @TestProcess
    fun testAction4(): String {
        return "testAction4"
    }

    @TestAction("noReturnValue")
    fun noReturnValue(){

    }

    @TestAction("testPathVar/{pathVar}")
    fun testPathVar(pathVar:String) = pathVar


}
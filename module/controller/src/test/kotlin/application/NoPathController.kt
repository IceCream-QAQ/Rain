package application

import controller.*
import rain.controller.ProcessFilter
import rain.controller.annotation.Before
import rain.controller.annotation.ProcessFilterBy
import java.lang.reflect.Method


class TestProcessFilter: ProcessFilter{
    override fun <T> invoke(
        provideAnnotation: Annotation,
        functionAnnotation: Annotation,
        controllerClass: Class<T>,
        controllerInstance: T,
        action: Method?
    ): Boolean {
        return !(action != null && action.name == "testAction3")
    }

}

@TestController
class NoPathController {

    @Before
    @ProcessFilterBy(TestProcessFilter::class)
    fun testBefore(){
        println("testBefore")
    }

    @TestAction("testAction")
    fun testAction(): String {
        return "testAction"
    }

    @TestAction1("testAction1")
    @Channel("test2")
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
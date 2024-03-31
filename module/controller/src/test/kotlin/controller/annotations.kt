package controller

import rain.api.annotation.LoadBy
import rain.controller.annotation.ProcessBy


@LoadBy(TestControllerLoader::class)
annotation class TestController

annotation class TestAction(val value: String, vararg val channel: String = ["test1", "test2", "test3", "test4"])

annotation class TestAction1(val value: String)
annotation class TestAction2(val value: String)
annotation class TestAction3(val value: String)
annotation class TestAction4(val value: String)


@ProcessBy(TestProcessProvider::class)
annotation class TestProcess
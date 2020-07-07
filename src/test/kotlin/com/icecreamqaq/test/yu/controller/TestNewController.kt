package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.NewDefaultController
import com.IceCreamQAQ.Yu.annotation.Path
import com.icecreamqaq.test.yu.annotation.TestHook

@NewDefaultController
class TestNewController {

    @Action("menu")
    fun menu() = "menu"

    @Action("m{abc}b{bbc}")
    fun mb(abc: String, bbc: String) = "mb: abc = $abc, bbc = $bbc."

    @After
    fun after(){
        println("TestNewControllerAfter")
    }

}

@Path("11")
@NewDefaultController
class TestNewController2 {

    @Action("menu/{ddd}")
    fun menu(ddd:String) = "menu: ddd = $ddd"

    @TestHook
    @Action("{ddd}/menu")
    fun menu2(ddd:String) = "menu2: ddd = $ddd"

    @Action("a{abc}b{bbc}")
    fun ab(abc: String, bbc: String) = "ab: abc = $abc, bbc = $bbc."

}
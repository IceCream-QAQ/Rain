package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.NewDefaultController
import com.IceCreamQAQ.Yu.annotation.Path

@NewDefaultController
class TestNewController {

    @Action("menu")
    fun menu() = "menu"

    @Action("m{abc}b{bbc}")
    fun mb(abc: String, bbc: String) = "mb: abc = $abc, bbc = $bbc."

    @Action("{id:BV.{10,10}}")
    fun bv(id: String) = id


}

@Path("11")
@NewDefaultController
class TestNewController2 {

    @Action("menu/{ddd}")
    fun menu(ddd: String) = "menu: ddd = $ddd"

    //    @TestHook
    @Action("{ddd}/menu")
    fun menu2(ddd: String) = "menu2: ddd = $ddd"

    @Action("a{abc}b{bbc}")
    fun ab(abc: String, bbc: String) = "ab: abc = $abc, bbc = $bbc."

}
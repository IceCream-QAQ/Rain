package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.*

@NewDefaultController
class TestNewController {

    @Action("menu")
    fun menu() = "menu"

    @Action("m{abc}b{bbc}")
    @Synonym(["c{abc}b{bbc}"])
    fun mb(abc: String, bbc: String) = "mb: abc = $abc, bbc = $bbc."

    @Action("{id:BV.{10,10}}")
    fun bv(id: String) = id

    @Action("co{id}", loadWeight = 1)
    fun co(id: String) = id

    @Action("coc{id}")
    fun coc(id: String) = id

    @After
    fun after(){
        println("TNC After!")
    }

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
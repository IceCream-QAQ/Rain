package com.icecreamqaq.test.yu.controller

import com.IceCreamQAQ.Yu.annotation.*

@DefaultController
class TestNewController {

    @Action("menu")
    fun menu() = "menu"

    @Action("m{abc}b{bbc}")
    @Synonym(["c{abc}b{bbc}"])
    fun mb(abc: String, bbc: String) = "mb: abc = $abc, bbc = $bbc."

    @Action("{id:BV.{10,10}}")
    fun bv(id: String): String {
        throw RuntimeException(id)
    }


    @Action("co{id}", loadPriority = 1)
    fun co(id: String) = id

    @Action("coc{id}")
    fun coc(id: String) = id

    @Before
    @Global
    fun before(){
        println("TNC Before!")
    }
    @Global
    @Before(weight = 2)
    fun before2(){
        println("TNC Before2!")
    }

    @After
    @Global
    fun after() {
        println("TNC After!")
    }

    @After(weight = 2)
    @Global
    fun after2() {
        println("TNC After2!")
    }

}

@Path("11")
@DefaultController
class TestNewController2 {

    @Action("menu/{ddd}")
    fun menu(ddd: String) = "menu: ddd = $ddd"

    //    @TestHook
    @Action("{ddd}/menu")
    fun menu2(ddd: String) = "menu2: ddd = $ddd"

    @Action("a{abc}b{bbc}")
    fun ab(abc: String, bbc: String) = "ab: abc = $abc, bbc = $bbc."

    @Global
    @Catch(error = Exception::class)
    fun catch(exception: Exception) {
        println(exception.cause)
    }

}
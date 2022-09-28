package com.IceCreamQAQ.Yu.loader

interface Loader {

    fun width(): Int = 10

//    fun load(items: Map<String, LoadItem>)
    fun load(items: Collection<LoadItem>)

}
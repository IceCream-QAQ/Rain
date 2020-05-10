package com.IceCreamQAQ.Yu.loader

interface Loader_ {

    fun width(): Int = 0

    fun load(items: Map<String, LoadItem_>)
}
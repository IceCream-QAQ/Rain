package com.IceCreamQAQ.Yu.loader

class LoadItem() {

    lateinit var loadBy: Any
    lateinit var type: Class<*>

    constructor(loadBy: Any, type: Class<*>) : this() {
        this.loadBy = loadBy
        this.type = type
    }
}
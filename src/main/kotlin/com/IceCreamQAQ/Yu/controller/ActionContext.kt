package com.IceCreamQAQ.Yu.controller

interface ActionContext {
    operator fun get(name: String): Any?
    operator fun set(name: String, obj: Any)
    fun remove(name: String): Any?
}
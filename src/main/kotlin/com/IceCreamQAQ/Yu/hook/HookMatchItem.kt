package com.IceCreamQAQ.Yu.hook

interface HookMatchItem {
    operator fun invoke(clazz: String, method: String, descriptor: String): Boolean
}
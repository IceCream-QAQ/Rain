package com.IceCreamQAQ.Yu.hook

interface HookMatchItem {
    operator fun invoke(clazz: String, method: String, descriptor: String): Boolean
}

class NoMatchHookItem @JvmOverloads constructor(
    val clazz: String,
    val method: String,
    val descriptor: String? = null
) : HookMatchItem {
    override fun invoke(clazz: String, method: String, descriptor: String): Boolean {
        if (clazz != this.clazz) return false
        if (method != this.method) return false
        this.descriptor?.let { if (descriptor != it) return false }
        return true
    }
}
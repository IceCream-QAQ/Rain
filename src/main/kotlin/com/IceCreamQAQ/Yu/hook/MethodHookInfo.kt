package com.IceCreamQAQ.Yu.hook

data class MethodHookInfo(
    val clazz: String,
    val method: String,
    val descriptor: String,
    val identifier: String,
    val statics: ArrayList<HookRunnableInfo> = ArrayList(),
    val instances: ArrayList<HookRunnableInfo> = ArrayList()
)
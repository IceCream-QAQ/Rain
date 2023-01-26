package com.IceCreamQAQ.Yu.hook

data class HookRunnableInfo(
    val className: String,
    val descriptor: String,
    val isInstanceMode: Boolean,
    val clazz: Class<out HookRunnable>
)
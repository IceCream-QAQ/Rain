package com.IceCreamQAQ.Yu.hook

data class HookRunnableInfo(
    val name: String,
    val instanceMode: Boolean,
    val clazz: Class<out HookRunnable>
)

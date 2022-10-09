package com.IceCreamQAQ.Yu.hook

data class ClassHookMethodInfo(
    val clazz: String,
    val methods: ArrayList<Pair<String, String?>> = ArrayList()
)
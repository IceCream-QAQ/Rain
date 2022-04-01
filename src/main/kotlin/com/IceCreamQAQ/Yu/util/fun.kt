package com.IceCreamQAQ.Yu.util

import java.util.*

internal fun <T> T.sout() = this.apply { println(this) }
internal fun uuid() = UUID.randomUUID().toString()

fun <K, V> MutableMap<K, V>.getOrPut(key: K, value: V): V {
    var v = get(key)
    if (v == null) {
        v = value
        put(key, v)
    }
    return v!!
}
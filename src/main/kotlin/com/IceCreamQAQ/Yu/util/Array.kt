package com.IceCreamQAQ.Yu.util

import com.IceCreamQAQ.Yu.controller.ActionInvoker
import kotlin.reflect.KProperty1

fun <T> MutableList<T>.bubbleSort(width: KProperty1<T, Int>) {
    for (i in 0 until this.size) {
        for (j in 0 until this.size - 1 - i) {
            val c = this[j]
            val n = this[j + 1]
            if (width.get(c) > width.get(n)) {
                this[j] = n
                this[j + 1] = c
            }
        }
    }
}

fun <T> MutableList<T>.bubbleSort(width: T.() -> Int) {
    for (i in 0 until this.size) {
        for (j in 0 until this.size - 1 - i) {
            val c = this[j]
            val n = this[j + 1]
            if (width(c) > width(n)) {
                this[j] = n
                this[j + 1] = c
            }
        }
    }
}
package com.IceCreamQAQ.Yu.util

import java.util.*

internal fun <T> T.sout() = this.apply { println(this) }
internal fun uuid() = UUID.randomUUID().toString()
package com.IceCreamQAQ.Yu.loader

import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer

interface IRainClassLoader {

    var hook: ClassTransformer

    fun define(name: String, data: ByteArray): Class<*>

    fun forName(name: String, initialize: Boolean): Class<*>
}
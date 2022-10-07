package com.IceCreamQAQ.Yu.loader

interface IRainClassLoader {
    fun define(name: String, data: ByteArray): Class<*>
}
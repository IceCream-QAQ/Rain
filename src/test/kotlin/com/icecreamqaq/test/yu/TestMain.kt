package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer
import java.util.regex.Pattern

fun main(args: Array<String>) {
    AppClassloader.registerTransformerList("com.icecreamqaq.test.yu.TestTransformer")
    TestStarter.start(args)
}

class TestTransformer : ClassTransformer {
    override fun transform(bytes: ByteArray, className: String): ByteArray {
        if (className == "com.IceCreamQAQ.Yu.DefaultApp") println(className)
        return bytes
    }
}

package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.hook.HookItem
import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.IceCreamQAQ.Yu.hook.YuHook
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer

fun main(args: Array<String>) {
    YuHook.put(HookItem("com.icecreamqaq.test.yu.controller.TestNewController", "mb", "com.icecreamqaq.test.yu.HookMb"))
    YuHook.put(HookItem("com.icecreamqaq.test.yu.controller.TestNewController", "mb", "com.icecreamqaq.test.yu.HookMb2"))
    YuHook.putMatchHookItem("com.icecreamqaq.test.yu.controller.TestNewController.*", "com.icecreamqaq.test.yu.HookMb3")
    AppClassloader.registerTransformerList("com.icecreamqaq.test.yu.TestTransformer")
    TestStarter.start(args)
}

class TestTransformer : ClassTransformer {
    override fun transform(bytes: ByteArray, className: String): ByteArray {
        if (className == "com.IceCreamQAQ.Yu.DefaultApp") println(className)
        return bytes
    }
}

class HookMb : HookRunnable {
    override fun preRun(method: HookMethod): Boolean {
        method.paras[2] = "Hook"
        return false
    }

    override fun postRun(method: HookMethod) {
    }

    override fun onError(method: HookMethod): Boolean {
        return true
    }
}

class HookMb2 : HookRunnable {

    override fun preRun(method: HookMethod): Boolean {
        method.paras[1] = "Yu"
        return false
    }

    override fun postRun(method: HookMethod) {
    }

    override fun onError(method: HookMethod): Boolean {
        return true
    }
}

class HookMb3 : HookRunnable {

    @Config("yu.webHelper.impl")
    private lateinit var test:String

    override fun preRun(method: HookMethod): Boolean {
        return false
    }

    override fun postRun(method: HookMethod) {
        method.result = method.result.toString() + " by hook. $test."
    }

    override fun onError(method: HookMethod): Boolean {
        return true
    }
}

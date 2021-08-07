package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.hook.*
import com.IceCreamQAQ.Yu.loader.AppClassloader


fun main(args: Array<String>) {
    YuHook.put(HookItem("com.icecream.test.Tc", "f", "com.icecreamqaq.test.yu.HookTa"))

    val appClassLoader = AppClassloader(TestStarter::class.java.classLoader)

    val tac = appClassLoader.loadClass("com.icecream.test.Tc")
    val ta = tac.newInstance()

    val g = tac.getMethod("f")
//    val g = tac.getMethod("g", Int::class.java, Int::class.java)

    println(g.invoke(ta))
}

open class Ta {
    //    companion object{
//        @JvmStatic
//
//    }
    fun g() = 2.toLong()
}

class Tb:Ta() {

    fun f() = super.g()
}


class HookTa : HookRunnable {
    override fun init(info: HookInfo) {
        println(info.className + "." + info.methodName)
    }

    override fun preRun(method: HookMethod): Boolean {
        method.result = 3

        return true
    }

    override fun postRun(method: HookMethod) {
    }

    override fun onError(method: HookMethod): Boolean {
        return true
    }
}

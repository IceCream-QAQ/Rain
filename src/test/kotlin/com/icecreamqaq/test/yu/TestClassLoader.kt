package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.hook.*
import com.IceCreamQAQ.Yu.loader.AppClassloader


fun main(args: Array<String>) {
    YuHook.put(HookItem("com.icecream.test.Tc", "f", "com.icecreamqaq.test.yu.HookTa"))

    val appClassLoader = AppClassloader(TestStarter::class.java.classLoader)

    val tac = appClassLoader.loadClass("com.icecream.test.Tc")
    val ta = tac.newInstance()

    val g = tac.getMethod("f", Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, Array<Int>::class.java)
//    val g = tac.getMethod("g", Int::class.java, Int::class.java)

    println(g.invoke(ta, 0, 0, arrayOf(0)))
}

open class Ta {
    //    companion object{
//        @JvmStatic
//
//    }
    fun f(group: Long, qq: Long, times: Array<Int>) = arrayListOf(1, 2, 3, 4)
}


class HookTa : HookRunnable {
    override fun init(info: HookInfo) {
//        println(info.className + "." + info.methodName)
    }

    override fun preRun(method: HookMethod): Boolean {
        method.result = arrayListOf(5, 6, 7, 8)

        return true
    }

    override fun postRun(method: HookMethod) {
    }

    override fun onError(method: HookMethod): Boolean {
        return true
    }
}

package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.Application
import com.IceCreamQAQ.Yu.ApplicationLauncher
import com.IceCreamQAQ.Yu.hook.HookItem
import com.IceCreamQAQ.Yu.hook.YuHook
import kotlinx.coroutines.newFixedThreadPoolContext

fun main(args: Array<String>) {
//    YuHook.put(HookItem("com.icecreamqaq.test.yu.controller.TestNewController", "mb", "com.icecreamqaq.test.yu.HookMb"))
    YuHook.put(HookItem("被 Hook 的类 的完整类名","被 Hook 的方法名","HookRunnable 的完整类名"))
    ApplicationLauncher.launch()
    Thread.sleep(5 * 60 * 1000)
}

//class HookMb : HookRunnable {
//    override fun preRun(method: HookMethod): Boolean {
//        method.paras[2] = "Hook"
//        return false
//    }
//
//    override fun postRun(method: HookMethod) {
//    }
//
//    override fun onError(method: HookMethod): Boolean {
//        return true
//    }
//}

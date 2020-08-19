package com.icecreamqaq.test.yu

fun main(args: Array<String>) {
//    YuHook.put(HookItem("com.icecreamqaq.test.yu.controller.TestNewController", "mb", "com.icecreamqaq.test.yu.HookMb"))
    TestStarter.start(args)
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

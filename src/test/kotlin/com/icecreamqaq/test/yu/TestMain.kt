package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.di.inject
import com.IceCreamQAQ.Yu.hook.HookItem
import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.IceCreamQAQ.Yu.hook.YuHook
import com.IceCreamQAQ.Yu.job.JobManager

fun main(args: Array<String>) {
    YuHook.put(HookItem("com.icecreamqaq.test.yu.controller.TestNewController", "mb", "com.icecreamqaq.test.yu.HookMb"))
    TestStarter.start(args)
    Thread.sleep(5 * 60 * 1000)
}

class HookMb : HookRunnable {

    val jobManager by inject<JobManager>()

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

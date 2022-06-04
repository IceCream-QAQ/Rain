package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.hook.HookItem
import com.IceCreamQAQ.Yu.hook.YuHook
import com.IceCreamQAQ.Yu.loader.Module

class TestModule : Module {
    override fun onLoad() {
        YuHook.put(HookItem("com.icecreamqaq.test.yu.TestEvent", "onStart", "com.icecreamqaq.test.yu.HookStartEvent"))
    }
}
package com.icecreamqaq.test.yu.di

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.di.kotlin.config
import com.IceCreamQAQ.Yu.event.events.AppStatusEvent

@EventListener
class KotlinByInject {

    val runMode by config<String>("yu.runMode")


    @Event
    fun onStart(e: AppStatusEvent.AppStarted) {
        println("[Di - KotlinByInject] OK!($runMode)")
    }

}


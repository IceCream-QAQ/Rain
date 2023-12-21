package com.icecreamqaq.test.yu.di

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStatusEvent
import javax.inject.Inject

@EventListener
class TestAutoBind {

    @Inject
    private lateinit var testInterface: TestInterface

    @Event
    fun onStart(e: AppStatusEvent.AppStarted) {
        println("[Di - AutoBind] ${testInterface()}")
    }

}


@AutoBind
interface TestInterface {
    operator fun invoke(): String
}

class TestInterfaceImpl : TestInterface {
    override fun invoke() = "AutoBind Success."

}
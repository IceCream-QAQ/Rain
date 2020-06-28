package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import javax.inject.Inject

@EventListener
class TestEvent {

    @Inject
    private lateinit var testInterface: TestInterface

    @Event
    fun onStart(e:AppStartEvent){
        testInterface.a()
    }

}


@AutoBind
interface TestInterface{
    fun a()
}

class TestInterfaceImpl:TestInterface{
    override fun a() {
        println("1231231231323")
    }

}
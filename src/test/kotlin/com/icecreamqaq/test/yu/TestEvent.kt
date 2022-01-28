package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.event.events.EventListenerRunExceptionEvent
import com.IceCreamQAQ.Yu.fullName
import com.IceCreamQAQ.Yu.job.JobManager
import com.IceCreamQAQ.Yu.job.JobManagerImpl
import com.IceCreamQAQ.Yu.util.Web
import javax.inject.Inject


@EventListener
class TestEvent {

    @Inject
    private lateinit var testInterface: TestInterface

    @Inject
    private lateinit var jobManager: JobManager

    @Inject
    private lateinit var web: Web

    @Event
    fun onStart(e: AppStartEvent) {
        error("Test Event Error!")
    }

    @Event
    fun onEventListenerError(e: EventListenerRunExceptionEvent){
        println("${e.listenerInfo.method.fullName} 产生异常: ${e.throwable::class.java.name}: ${e.throwable.message}.")
    }

}



@AutoBind
interface TestInterface {
    fun a()
}

class TestInterfaceImpl : TestInterface {
    override fun a() {
        println("1231231231323")
    }

}
package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.event.events.AppStopEvent
import com.IceCreamQAQ.Yu.event.events.EventListenerRunExceptionEvent
import com.IceCreamQAQ.Yu.event.events.JobRunExceptionEvent
import com.IceCreamQAQ.Yu.fullName
import com.IceCreamQAQ.Yu.job.JobManager
import com.IceCreamQAQ.Yu.job.JobManagerImpl
import com.IceCreamQAQ.Yu.util.Web
import javax.inject.Inject

class CustomEvent : com.IceCreamQAQ.Yu.event.events.Event() {
    override fun cancelAble() = true
}

@EventListener
class TestEvent {

    @Inject
    private lateinit var testInterface: TestInterface

    @Inject
    private lateinit var jobManager: JobManager

    @Inject
    private lateinit var web: Web

    @Inject
    private lateinit var eventBus: EventBus

    @Event
    fun onStart(e: AppStartEvent) {
        println("Baidu:")
//        println(web.get("https://www.baidu.com/"))
        println("Baidu.")

        eventBus.post(CustomEvent())
        jobManager.registerTimer(1000) {
            error("Test Job Error!")
        }
    }

    @Event
    fun onClose(e: AppStopEvent) {
        println("AppStopEvent")
    }

    @Event(weight = Event.Weight.record)
    fun customListenerR(e: CustomEvent) {
        println("On CustomEvent at record level.")
    }

    @Event(weight = Event.Weight.highest)
    fun customListenerHH(e: CustomEvent) {
        println("On CustomEvent at highest level.")
    }

    @Event(weight = Event.Weight.high)
    fun customListenerH(e: CustomEvent) {
        println("On CustomEvent at high level.")
    }

    @Event(weight = Event.Weight.normal)
    fun customListenerN(e: CustomEvent) {
        println("On CustomEvent at normal level.")
    }

    @Event(weight = Event.Weight.low)
    fun customListenerL(e: CustomEvent) {
        e.cancel = true
        println("On CustomEvent at low level.")
    }

    @Event(weight = Event.Weight.lowest)
    fun customListenerLL(e: CustomEvent) {
        println("On CustomEvent at lowest level.")
    }

    @Event
    fun onEventListenerError(e: EventListenerRunExceptionEvent) {
        println("${e.listenerInfo.method.fullName} 产生异常: ${e.throwable::class.java.name}: ${e.throwable.message}.")
    }

    @Event
    fun onJobError(e: JobRunExceptionEvent) {
        println("${e.jobInfo.name} 产生异常: ${e.throwable::class.java.name}: ${e.throwable.message}.")
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
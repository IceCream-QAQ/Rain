package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.events.*
import com.IceCreamQAQ.Yu.fullName
import com.IceCreamQAQ.Yu.job.JobManager
import com.IceCreamQAQ.Yu.util.Web
import com.IceCreamQAQ.Yu.validation.Min
import javax.inject.Inject

class CustomEvent : AbstractCancelAbleEvent()

@EventListener
class TestEvent {

    @Inject
    private lateinit var jobManager: JobManager

    @Inject
    private lateinit var web: Web

    @Inject
    private lateinit var eventBus: EventBus

    @Event
    fun onStart(e: AppStatusEvent.AppStarted) {
        println("Baidu:")
        println(web.get("https://www.baidu.com/"))
        println("Baidu.")

        eventBus.post(CustomEvent())
        jobManager.registerTimer(1000) {
            error("Test Job Error!")
        }
        testParamValid(51)
        testParamValid(50)
    }

    fun testParamValid(@Min(50) a: Int) {
        println(a)
    }

    @Event
    fun onClose(e: AppStatusEvent.AppStopping) {
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
        e.isCanceled = true
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


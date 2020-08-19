package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.job.JobManager
import com.IceCreamQAQ.Yu.toJSONObject
import com.IceCreamQAQ.Yu.toJSONString
import com.IceCreamQAQ.Yu.util.Web
import okhttp3.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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
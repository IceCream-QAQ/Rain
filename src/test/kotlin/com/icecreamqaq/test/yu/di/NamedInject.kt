package com.icecreamqaq.test.yu.di

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import javax.inject.Inject

import javax.inject.Named

/***
 * @description : Todo
 * @author : 梦某人
 * @date : 2023/2/17 17:26 
 */
@AutoBind
interface TestNamed {
    operator fun invoke(): String
}

@Named("test")
class TestNamedImpl: TestNamed {
    override fun invoke() = "Test1"
}


@Named("test2")
class TestNamedImpl2: TestNamed {
    override fun invoke() = "Test2"
}

@EventListener
class NamedInjectTest{

    @Inject
    @field:Named("{test.named}")
    private lateinit var testNamed: TestNamed

    @Event
    fun onStart(e: AppStartEvent){
        println("On Start! in NamedInject")
        println("[Di - Named Inject ${testNamed()}]")
    }
}

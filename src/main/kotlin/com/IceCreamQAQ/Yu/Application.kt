package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.`as`.AsLoader
import com.IceCreamQAQ.Yu.di.YuContext.Companion.getBean
import com.IceCreamQAQ.Yu.di.config.impl.ConfigImpl
import com.IceCreamQAQ.Yu.di.getBean
import com.IceCreamQAQ.Yu.di.impl.ContextImpl
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.event.events.AppStopEvent
import com.IceCreamQAQ.Yu.loader.AppLoader
import com.IceCreamQAQ.Yu.util.exists
import java.util.*

class Application {

    lateinit var eventBus: EventBus
    lateinit var asLoader: AsLoader

    fun start() {
        val appClassloader = this::class.java.classLoader
        val runMode = System.getProperty("yu.runMode")?.lowercase(Locale.getDefault())
            ?: if (exists("pom.xml", "build.gradle", "build.gradle.kts")) "dev"
            else null
        val launchPackage = System.getProperty("yu.launchPackage")

        val configManager = ConfigImpl(appClassloader, runMode, launchPackage).init()
        val context = ContextImpl(appClassloader, configManager).init()

        context.getBean<AppLoader> { error("Application 初始化过程中未能正确获取 Loader！") }.load()
        asLoader = context.getBean()!!

        asLoader.start()
        eventBus = context.getBean()!!
        eventBus.post(AppStartEvent())

        Runtime.getRuntime().addShutdownHook(Thread { stop() })
    }

    fun stop() {
        eventBus.post(AppStopEvent())
        asLoader.stop()
    }

}
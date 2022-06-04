package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.di.YuContext.Companion.getBean
import com.IceCreamQAQ.Yu.di.config.impl.ConfigImpl
import com.IceCreamQAQ.Yu.di.impl.ContextImpl
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.loader.AppLoader
import com.IceCreamQAQ.Yu.util.exists
import java.util.*

class Application {

    lateinit var eventBus: EventBus

    fun start() {
        val appClassloader = this::class.java.classLoader
        val runMode = System.getProperty("yu.runMode")?.lowercase(Locale.getDefault())
            ?: if (exists("pom.xml", "build.gradle", "build.gradle.kts")) "prod"
            else null
        val launchPackage = System.getProperty("yu.launchPackage")

        val configManager = ConfigImpl(appClassloader, runMode, launchPackage)
        val context = ContextImpl(appClassloader, configManager)

        (context.getBean<AppLoader>() ?: error("Application 初始化过程中未能正确获取 Loader！")).load()

        eventBus = context.getBean()!!
        eventBus.post(AppStartEvent())
    }

    fun stop(){
        
    }

}
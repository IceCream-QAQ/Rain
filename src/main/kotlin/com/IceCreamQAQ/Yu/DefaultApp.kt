package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.`as`.AsLoader
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.ConfigManagerDefaultImpl
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.EventBusImpl
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.event.events.AppStopEvent
import com.IceCreamQAQ.Yu.loader.AppLoader
import com.IceCreamQAQ.Yu.module.ModuleManager
import javax.inject.Inject

@NotSearch
open class DefaultApp {

    @Inject
    lateinit var moduleManager: ModuleManager

    @Inject
    lateinit var loader: AppLoader

    @Inject
    lateinit var asLoader: AsLoader

    val context:YuContext

    lateinit var eventBus: EventBus

    init {
        val logger = PrintAppLog()

        val appClassloader = DefaultApp::class.java.classLoader
        val configManager = ConfigManagerDefaultImpl(appClassloader, logger, System.getProperty("yu-runMode"))
        context = YuContext(configManager, logger)

        context.putBean(ClassLoader::class.java, "appClassLoader", appClassloader)

        context.injectBean(this)
    }

    @Inject
    fun init(){
        moduleManager.loadModule()
    }

    fun start(){
        loader.load()
        asLoader.start()

        eventBus = context[EventBus::class.java]!!
        eventBus.post(AppStartEvent())

        Runtime.getRuntime().addShutdownHook(Thread { stop() })
    }

    fun stop(){
        eventBus.post(AppStopEvent())
        asLoader.stop()
    }

    class PrintAppLog : AppLogger{
        override fun logDebug(title: String?, body: String?): Int {
            println("------ Log Debug ------:: $title\t\t: $body")
            return 0
        }

        override fun logInfo(title: String?, body: String?): Int {
            println("------ Log Info ------:: $title\t\t: $body")
            return 0
        }

        override fun logWarning(title: String?, body: String?): Int {
            println("------ Log Warning ------:: $title\t\t: $body")
            return 0
        }

        override fun logError(title: String?, body: String?): Int {
            System.err.println("------ Log Error ------:: $title\t\t: $body")
            return 0
        }

        override fun logFatal(title: String?, body: String?): Int {
            System.err.println("------ Log Error ------:: $title\t\t: $body")
            return 0
        }
    }
}
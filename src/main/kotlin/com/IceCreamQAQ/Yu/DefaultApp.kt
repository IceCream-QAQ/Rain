package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.`as`.AsLoader
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.ConfigManager
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.event.EventBus
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.IceCreamQAQ.Yu.loader.AppLoader
import javax.inject.Inject
import kotlin.system.exitProcess

@NotSearch
open class DefaultApp {

    @Inject
    lateinit var loader: AppLoader

    @Inject
    lateinit var asLoader: AsLoader

    @Inject
    lateinit var eventBus: EventBus

    init {
        val logger = PrintAppLog()

        val appClassloader = DefaultApp::class.java.classLoader
        val configManager = ConfigManager(appClassloader, logger, System.getProperty("yu-runMode"))
        val context = YuContext(configManager, logger)

        context.putBean(ClassLoader::class.java, "appClassLoader", appClassloader)

        context.injectBean(this)

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            asLoader.stop()
        }))
    }

    fun start(){
        loader.load()
        asLoader.start()
        eventBus.post(AppStartEvent())
    }

    fun stop(){
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
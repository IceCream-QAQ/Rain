package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.di.ConfigManager
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.AppLoader_
import javax.inject.Inject

class DefaultApp {

    @Inject
    private lateinit var loader: AppLoader_

    init {
        val logger = PrintAppLog()

        val appClassloader = DefaultApp::class.java.classLoader
        val configManager = ConfigManager(appClassloader, logger, null)
        val context = YuContext(configManager, logger)

        context.putBean(ClassLoader::class.java, "appClassLoader", appClassloader)

        context.injectBean(this)
    }

    fun start(){
        loader.load()
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
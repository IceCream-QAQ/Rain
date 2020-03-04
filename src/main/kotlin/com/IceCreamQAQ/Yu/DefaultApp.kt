package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.annotation.Inject
import com.IceCreamQAQ.Yu.di.ConfigManager
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.AppLoader_

class DefaultApp {

    @Inject
    private lateinit var loader: AppLoader_

    fun load(){
        loader.load()
    }

    companion object{

        fun start(){
            val logger = PrintAppLog()

            val appClassloader = AppClassloader(DefaultApp::class.java.classLoader, logger)
            val configer = ConfigManager(appClassloader, logger, null)
            val context = YuContext(configer, logger)

            context.putBean(ClassLoader::class.java, "appClassLoader", appClassloader)

            val app = context.newBean(DefaultApp::class.java,save = true)!!
            app.load()
        }

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
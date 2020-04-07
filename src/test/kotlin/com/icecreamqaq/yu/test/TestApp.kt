package com.icecreamqaq.yu.test

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.controller.DefaultActionContext
import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.RouterPlus
import com.IceCreamQAQ.Yu.di.ConfigManager
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.job.JobManager_
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.AppLoader_
import com.icecreamqaq.yu.test.util.TestUtil
import javax.inject.Inject

class TestApp {

    @Inject
    private lateinit var loader: AppLoader_

    @Inject
    private lateinit var jobManager: JobManager_

    fun load(){
        loader.load()

        jobManager.start()
    }

    companion object{

        @JvmStatic
        fun start(){
            val logger = PrintAppLog()

            val appClassloader = AppClassloader(TestApp::class.java.classLoader, logger)
            val configer = ConfigManager(appClassloader, logger, null)
            val context = YuContext(configer, logger)

            context.putBean(ClassLoader::class.java, "appClassLoader", appClassloader)
            context.putBean(AppClassloader::class.java, "appClassLoader", appClassloader)

            val app = context.newBean(TestApp::class.java,save = true)!!
            app.load()

            val test = context.getBean(TestUtil::class.java)
            println(test)

            val router = context.getBean(RouterPlus::class.java,"priv") ?: return

            val ac = DefaultActionContext()

            val paths = arrayOf("test","ttt")
            ac.setPath(paths)

            router.invoke(paths[0],ac)
//            router.invoke()
        }

    }

    class PrintAppLog : AppLogger {
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
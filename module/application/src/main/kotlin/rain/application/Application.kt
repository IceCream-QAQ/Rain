package rain.application

import rain.api.event.EventBus
import rain.api.di.getBean
import rain.api.di.DiContext.Companion.getBean
import rain.application.events.AppStatusEvent
import rain.application.loader.AppLoader
import rain.application.loader.ApplicationServiceLoader
import rain.classloader.AppClassloader
import rain.classloader.IRainClassLoader
import rain.di.impl.ContextImpl
import rain.di.config.impl.ConfigImpl
import rain.di.impl.LocalInstanceClassContext
import rain.di.impl.NoInstanceClassContext
import rain.function.exists
import java.util.*

class Application {

    var eventBus: EventBus? = null
    lateinit var asLoader: ApplicationServiceLoader

    fun start() {
        val appClassloader = this::class.java.classLoader
        val runMode = System.getProperty("rain.runMode")?.lowercase(Locale.getDefault())
            ?: if (exists("pom.xml", "build.gradle", "build.gradle.kts")) "dev"
            else null
        val launchPackage = System.getProperty("rain.launchPackage")

        val configManager = ConfigImpl(appClassloader, runMode, launchPackage).init()
        val context = ContextImpl(appClassloader, configManager).init()

        if (appClassloader is AppClassloader){
            context.contextMap[IRainClassLoader::class.java] = NoInstanceClassContext(context, IRainClassLoader::class.java)
                .apply {
                    putBinds("", LocalInstanceClassContext(appClassloader))
                }
        }

        context.getBean<AppLoader> { error("Application 初始化过程中未能正确获取 Loader！") }.load()
        asLoader = context.getBean()!!

        asLoader.start()
        eventBus = context.getBean()
        eventBus?.post(AppStatusEvent.AppStarted())

        Runtime.getRuntime().addShutdownHook(Thread { stop() })
    }

    fun stop() {
        eventBus?.post(AppStatusEvent.AppStopping())
        asLoader.stop()
    }

}
import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.di.ConfigManager
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.AppLoader_
import java.io.File

class Index {
    companion object {


    }
}

fun main(args: Array<String>) {

    val logger = TestAppLogger()

    val appClassloader = AppClassloader(Index.javaClass.classLoader, logger)
    val configer = ConfigManager(appClassloader, logger, null)
    val context = YuContext(configer, logger)

    context.putBean(ClassLoader::class.java, "appClassLoader", appClassloader)

    val loader = context.newBean(AppLoader_::class.java)

    loader!!.load()

}


class TestAppLogger : AppLogger {
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
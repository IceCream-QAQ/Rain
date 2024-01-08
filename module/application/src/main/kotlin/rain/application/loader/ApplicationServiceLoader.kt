package rain.application.loader

import org.slf4j.LoggerFactory
import rain.api.di.DiContext
import rain.api.di.DiContext.Companion.get
import rain.api.loader.ApplicationService
import rain.api.loader.LoadItem
import rain.api.loader.Loader
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ApplicationServiceLoader : Loader {
    @Inject
    private lateinit var context: DiContext

    private lateinit var instances: Array<ApplicationService>
    private val log = LoggerFactory.getLogger(ApplicationServiceLoader::class.java)

    override fun load(items: Collection<LoadItem>) {
        val list = ArrayList<ApplicationService>()
        for (item in items) {
            val a = context[item.clazz] as? ApplicationService ?: continue
            list.add(a)
        }
        val instances = list.toTypedArray()

        Arrays.sort(instances) { a, b -> a.priority() - b.priority() }
        this.instances = instances

        log.info("[应用服务] 开始加载应用服务。")
        for (instance in instances) {
            try {
                log.debug("[应用服务] 初始化应用服务: ${instance::class.simpleName}.")
                context.injectBean(instance)
                instance.init()
                log.info("[应用服务] 初始化应用服务: ${instance::class.simpleName} 成功!")
            } catch (e: Exception) {
                log.error("[应用服务] 初始化应用服务: ${instance::class.simpleName} 出错!", e)
                throw e
            }
        }
        log.info("[应用服务] 应用服务加载完成!")
    }

    fun start() {
        log.info("[应用服务] 开始启动应用服务。")
        for (instance in instances) {
            try {
                log.debug("[应用服务] 启动应用服务: ${instance::class.simpleName}.")
                context.injectBean(instance)
                instance.start()
                log.info("[应用服务] 启动应用服务: ${instance::class.simpleName} 成功!")
            } catch (e: Exception) {
                log.error("[应用服务] 启动应用服务: ${instance::class.simpleName} 出错!", e)
                throw e
            }
        }
        log.info("[应用服务] 应用服务启动完成!")
    }

    fun stop() {
        log.info("[应用服务] 开始停止应用服务。")
        for (instance in instances) {
            try {
                log.debug("[应用服务] 停止应用服务: ${instance::class.simpleName}.")
                instance.stop()
                log.info("[应用服务] 停止应用服务: ${instance::class.simpleName} 成功!")
            } catch (e: Exception) {
                log.error("[应用服务] 停止应用服务: ${instance::class.simpleName} 出错!", e)
                throw e
            }
        }
        log.info("[应用服务] 应用服务停止完成!")
    }

    override fun priority(): Int = 2

    @Deprecated("过时方法", ReplaceWith("priority"))
    fun width(): Int = priority()
}
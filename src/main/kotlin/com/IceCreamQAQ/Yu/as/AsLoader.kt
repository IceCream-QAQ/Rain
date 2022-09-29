package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.YuContext.Companion.get
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AsLoader : Loader {

    @Inject
    private lateinit var context: YuContext

    private lateinit var instances: Array<ApplicationService>
    private val log = LoggerFactory.getLogger(AsLoader::class.java)

    override fun load(items: Collection<LoadItem>) {
        val list = ArrayList<ApplicationService>()
        for (item in items) {
            val a = context[item.clazz] as? ApplicationService ?: continue
            list.add(a)
        }
        val instances = list.toTypedArray()

        Arrays.sort(instances) { a, b -> a.width() - b.width() }
        this.instances = instances

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
    }

    fun start() {
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
    }

    fun stop() {
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
    }

    override fun priority(): Int = 2

    @Deprecated("过时方法", ReplaceWith("priority"))
    fun width(): Int = priority()

}
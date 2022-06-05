package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.YuContext.Companion.get
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import javax.inject.Inject

class AsLoader : Loader {

    @Inject
    private lateinit var context: YuContext

    private lateinit var instances: Array<ApplicationService>
    private val log = LoggerFactory.getLogger(AsLoader::class.java)

    override fun load(items: Map<String, LoadItem>) {
        val list = ArrayList<ApplicationService>()
        for (item in items.values) {
            val a = context[item.clazz] as? ApplicationService ?: continue
            list.add(a)
        }
        val instances = list.toTypedArray()

        for (i in instances.indices) {
            for (j in 0 until instances.size - 1 - i) {
                val c = instances[j]
                val n = instances[j + 1]
                if (c.width() > n.width()) {
                    instances[j] = n
                    instances[j + 1] = c
                }
            }
        }

        this.instances = instances

        for (instance in instances) {
            try {
                log.debug("Init ApplicationService: ${instance::class.simpleName}.")
                context.injectBean(instance)
                instance.init()
                log.info("Init ApplicationService: ${instance::class.simpleName} Success!")
            } catch (e: Exception) {
                log.error("Init ApplicationService: ${instance::class.simpleName} Error!", e)
                throw e
            }
        }
    }

    fun start() {
        for (instance in instances) {
            try {
                log.debug("Start ApplicationService: ${instance::class.simpleName}.")
                context.injectBean(instance)
                instance.start()
                log.info("Start ApplicationService: ${instance::class.simpleName} Success!")
            } catch (e: Exception) {
                log.error("Start ApplicationService: ${instance::class.simpleName} Error!", e)
                throw e
            }
        }
    }

    fun stop() {
        for (instance in instances) {
            try {
                log.debug("Stop ApplicationService: ${instance::class.simpleName}.")
                instance.stop()
                log.info("Stop ApplicationService: ${instance::class.simpleName} Success!")
            } catch (e: Exception) {
                log.error("Stop ApplicationService: ${instance::class.simpleName} Error!", e)
                throw e
            }
        }
    }

    override fun width(): Int = 2

}
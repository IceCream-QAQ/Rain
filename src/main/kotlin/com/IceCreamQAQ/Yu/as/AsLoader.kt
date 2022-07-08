package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.di.YuContext
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

    override fun load(items: Map<String, LoadItem>) {
        val list = ArrayList<ApplicationService>()
        for (item in items.values) {
            val a = context[item.type] as? ApplicationService ?: continue
            list.add(a)
        }
        val instances = list.toTypedArray()

        Arrays.sort(instances) { a, b -> a.width() - b.width() }
        this.instances = instances

        for (instance in instances) {
            try {
                log.debug("Init ApplicationService: ${instance::class.simpleName}.")
                context.populateBean(instance)
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
                context.populateBean(instance)
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

    override fun priority(): Int = 2

    @Deprecated("过时方法", ReplaceWith("priority"))
    fun width(): Int = priority()

}
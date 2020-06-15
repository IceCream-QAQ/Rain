package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import javax.inject.Inject

class EventListenerLoader : Loader {

    private val log = LoggerFactory.getLogger(EventListenerLoader::class.java)

    @Inject
    private lateinit var logger: AppLogger

    @Inject
    private lateinit var eventBus: EventBus

    @Inject
    private lateinit var context: YuContext


    override fun load(items: Map<String, LoadItem>) {

        for (item in items.values) {
            log.info("Register EventListener: ${item.type.name}.")
            try {
                eventBus.register(context.getBean(item.type, ""))
                log.info("Register EventListener: ${item.type.name} Success!")
            } catch (e: Exception) {
                e.printStackTrace()
                log.info("Register EventListener: ${item.type.name} Fail!", e)
            }
        }
    }

    override fun width(): Int {
        return 10
    }
}
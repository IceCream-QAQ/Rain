package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import javax.inject.Inject

open class EventListenerLoader : Loader {

    companion object {
        private val log = LoggerFactory.getLogger(EventListenerLoader::class.java)
    }

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var context: YuContext


    override fun load(items: Map<String, LoadItem>) {

        for (item in items.values) {
            if (!item.type.isBean()) continue
            log.debug("Register EventListener: ${item.type.name}.")
            try {
                register(item.type, context.getBean(item.type, "")!!)
                log.info("Register EventListener: ${item.type.name} Success!")
            } catch (e: Exception) {
                e.printStackTrace()
                log.error("Register EventListener: ${item.type.name} Fail!", e)
            }
        }
    }

    @Inject
    lateinit var creator: EventInvokerCreator

    open fun register(clazz: Class<*>, instance: Any) {
        clazz.methods.forEach {
            it.annotation<Event> {
                eventBus.register(
                    EventListenerInfo(
                        clazz = clazz,
                        method = it,
                        weight = weight,
                        instance = instance,
                        invoker = creator
                            .createEventHandlerInvokerClass(it)
                            .run { getConstructor(Any::class.java).newInstance(instance) }
                                as EventInvoker
                    )
                )
            }
        }
    }

    override fun width(): Int {
        return 10
    }
}
package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import javax.inject.Inject

class EventListenerLoader : Loader {

    @Inject
    private lateinit var logger: AppLogger
    @Inject
    private lateinit var eventBus: EventBus
    @Inject
    private lateinit var context: YuContext


    override fun load(items: Map<String, LoadItem>) {
        for (item in items.values) {
            try {
                eventBus.register(context.getBean(item.type, ""))
            } catch (e: Exception) {
                e.printStackTrace()
                logger.logError("YuQ Loader", "EventHandler " + item.type.name + " 注册失败！")
            }
        }
    }

    override fun width(): Int {
        return 10
    }
}
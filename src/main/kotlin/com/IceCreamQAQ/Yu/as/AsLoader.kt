package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem_
import com.IceCreamQAQ.Yu.loader.Loader_
import javax.inject.Inject

class AsLoader : Loader_ {

    @Inject
    private lateinit var context: YuContext

    private lateinit var instances: List<ApplicationService>

    override fun load(items: Map<String, LoadItem_>) {
        val list = ArrayList<ApplicationService>()
        for (item in items.values) {
            val a = context[item.type] as? ApplicationService ?: continue
            a.init()
            list.add(a)
        }
        instances = list
    }

    fun start() {
        for (instance in instances) {
            context.injectBean(instance)
            instance.start()
        }
    }

    fun stop() {
        for (instance in instances) instance.stop()
    }

    override fun width(): Int = 2

}
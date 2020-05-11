package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.loader.LoadItem_
import com.IceCreamQAQ.Yu.loader.Loader_
import javax.inject.Inject

class BeanFactoryLoader :Loader_ {

    @Inject
    lateinit var manager: BeanFactoryManager

    override fun width(): Int = 1

    override fun load(items: Map<String, LoadItem_>) {
        for (item in items.values) {
            manager.registerFactory(item.type as Class<BeanFactory<*>>)
        }
    }
}
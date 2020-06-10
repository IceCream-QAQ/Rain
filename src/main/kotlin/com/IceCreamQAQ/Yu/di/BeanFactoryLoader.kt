package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import javax.inject.Inject

class BeanFactoryLoader :Loader {

    @Inject
    lateinit var manager: BeanFactoryManager

    override fun width(): Int = 1

    override fun load(items: Map<String, LoadItem>) {
        for (item in items.values) {
            manager.registerFactory(item.type as Class<BeanFactory<*>>)
        }
    }
}
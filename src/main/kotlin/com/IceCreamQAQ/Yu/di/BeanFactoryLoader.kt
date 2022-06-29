package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import javax.inject.Inject

class BeanFactoryLoader : Loader {

    @Inject
    lateinit var manager: BeanFactoryManager

    override fun priority(): Int = 1

    @Suppress("UNCHECKED_CAST")
    override fun load(items: Map<String, LoadItem>) {
        for (item in items.values) {
            if (item.type.isBean()) manager.registerFactory(item.type as Class<BeanFactory<*>>)
        }
    }
}
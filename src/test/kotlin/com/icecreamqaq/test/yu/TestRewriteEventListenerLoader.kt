package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.event.EventListenerLoader
import com.IceCreamQAQ.Yu.loader.LoadItem

class TestRewriteEventListenerLoader : EventListenerLoader() {
    override fun load(items: Map<String, LoadItem>) {
        println("RewriteEventListenerLoader!")
        super.load(items)
    }
}
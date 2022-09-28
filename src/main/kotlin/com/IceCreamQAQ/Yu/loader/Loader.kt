package com.IceCreamQAQ.Yu.loader

interface Loader : Comparable<Loader> {

    fun priority(): Int = 10

//    fun load(items: Map<String, LoadItem>)
    fun load(items: Collection<LoadItem>)

    override fun compareTo(other: Loader): Int = this.priority() - other.priority()
}
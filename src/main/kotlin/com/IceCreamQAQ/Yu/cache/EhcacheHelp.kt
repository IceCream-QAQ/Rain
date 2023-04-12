package com.IceCreamQAQ.Yu.cache

import org.ehcache.Cache

class EhcacheHelp<T>(private val cache: Cache<String, Any>) : Iterable<Map.Entry<String, T>> {

    operator fun get(key: String): T? {
        return cache.get(key) as? T?
    }

    operator fun set(key: String, value: T): T {
        cache.put(key, value)
        return value
    }

    fun getOrDefault(key: String, defaultValue: T): T =
        cache.get(key) as? T? ?: defaultValue

    fun getOrDefault(key: String, defaultValue: () -> T): T =
        cache.get(key) as? T? ?: defaultValue()

    fun getOrPut(key: String, value: T): T =
        cache.get(key) as? T? ?: set(key, value)

    fun getOrPut(key: String, value: () -> T): T =
        cache.get(key) as? T? ?: set(key, value())


    fun remove(key: String) {
        cache.remove(key)
    }

    fun removeAll() {
        cache.removeAll { true }
    }

    override fun iterator(): Iterator<Map.Entry<String, T>> {
        return object : Iterator<Map.Entry<String, T>> {
            //            val iter =
            val iterator = cache.iterator()

            inner class Entry<T>(override val key: String, override val value: T) : Map.Entry<String, T>

            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next() = iterator.next()!!.let { Entry(it.key, it.value!! as T) }

        }
    }

}
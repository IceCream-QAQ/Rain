package com.IceCreamQAQ.Yu.cache

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.config.ConfigManager
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.core.EhcacheManager
import org.ehcache.xml.XmlConfiguration
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

@NotSearch
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

class EhcacheHelpFactory : BeanFactory<EhcacheHelp<*>>, ApplicationService {

    @Config("yu.cache.ehcache.config")
    private var ehcacheConfigLocation: String? = null
    private var cm: CacheManager? = null

    @Inject
    private lateinit var configManager: ConfigManager

    @Inject
    private lateinit var context: YuContext

    @Inject
    @field:Named("appClassloader")
    private lateinit var classLoader: ClassLoader

    private var cmDefaultMap = ConcurrentHashMap<String, CacheManager>()

    @Deprecated("过时方法", replaceWith = ReplaceWith("priority"))
    override fun width() = 5
    override val type = EhcacheHelp::class.java

    override fun createBean(name: String): EhcacheHelp<*>? {
        return EhcacheHelp<Any>(
            cm?.getCache(name, String::class.java, Any::class.java)
                ?: cmDefaultMap.getOrPut(name) {
                    configManager.getConfig("yu.cache.ehcache.caches.$name.default", String::class.java)
                        ?.let { classLoader.getResource(it) }
                        ?.let { EhcacheManager(XmlConfiguration(it, classLoader)) }
                        ?.apply { init() }
                }?.getCache(name, String::class.java, Any::class.java) ?: return null
        )
    }

    override fun init() {
        val url = ehcacheConfigLocation?.let { javaClass.classLoader.getResource(it) } ?: return
        cm = EhcacheManager(XmlConfiguration(url, classLoader))
        cm!!.init()
    }

    override fun start() {

    }

    override fun stop() {
        cm?.close()
        for (cache in cmDefaultMap.values) {
            cache.close()
        }
    }




}


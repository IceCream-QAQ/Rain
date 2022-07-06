package com.IceCreamQAQ.Yu.cache

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.ConfigManagerDefaultImpl
import com.IceCreamQAQ.Yu.di.YuContext
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

@NotSearch
class EhcacheHelp<T>(private val cache: Cache) : Iterable<Map.Entry<String, T>> {

    operator fun get(key: String): T? {
        return cache.get(key)?.objectValue as? T?
    }

    operator fun set(key: String, value: T): T {
        cache.put(Element(key, value))
        return value
    }

    fun getOrDefault(key: String, defaultValue: T): T =
        cache.get(key)?.objectValue as? T? ?: defaultValue

    fun getOrDefault(key: String, defaultValue: () -> T): T =
        cache.get(key)?.objectValue as? T? ?: defaultValue()

    fun getOrPut(key: String, value: T): T =
        cache.get(key)?.objectValue as? T? ?: set(key, value)

    fun getOrPut(key: String, value: () -> T): T =
        cache.get(key)?.objectValue as? T? ?: set(key, value())


    fun remove(key: String) {
        cache.remove(key)
    }

    fun removeAll(){
        cache.removeAll()
    }

    override fun iterator(): Iterator<Map.Entry<String, T>> {
        return object : Iterator<Map.Entry<String, T>> {
            //            val iter =
            val keys = cache.keys.iterator()

            inner class Entry<T>(override val key: String, override val value: T) : Map.Entry<String, T>

            override fun hasNext(): Boolean = keys.hasNext()

            override fun next() = keys.next()!!.let { Entry(it as String, get(it)!!) }

        }
    }

}

class EhcacheHelpFactory : BeanFactory<EhcacheHelp<*>?>, ApplicationService {

    @Config("yu.cache.ehcache.config")
    @Default("ehcache-yu-default.xml")
    private lateinit var ehcacheConfigLocation: String
    private var cm: CacheManager? = null

    @Inject
    private lateinit var configManager: ConfigManagerDefaultImpl

    @Inject
    private lateinit var context: YuContext

    @Inject
    @field:Named("appClassLoader")
    private lateinit var classLoader: ClassLoader

    private var cmDefaultMap = ConcurrentHashMap<String, CacheManager>()

    override fun width() = 5

    override fun createBean(clazz: Class<EhcacheHelp<*>?>, name: String): EhcacheHelp<*>? {
        val cache = cm?.getCache(name) ?: {
            val cm = cmDefaultMap[name] ?: {
                val location = configManager.get("yu.cache.ehcache.caches.$name.default", String::class.java)
                if (location != null) {
                    val url = classLoader.getResource(location)
                    if (url != null) {
                        val cm = CacheManager.newInstance(url) ?: null
                        if (cm != null) cmDefaultMap[name] = cm
                        cm
                    } else {
                        println("url: $location is null")
                        null
                    }
                } else {
                    println("location: $name is null")
                    null
                }
            }()
            cm?.getCache(name)
        }() ?: return null
        return EhcacheHelp<Any>(cache)
    }

    override fun init() {
//        context.register(EhcacheHelp::class.java, true)

        val url = javaClass.classLoader.getResource(ehcacheConfigLocation) ?: return
        cm = CacheManager.newInstance(url)
    }

    override fun start() {

    }

    override fun stop() {
        cm?.shutdown()
        for (cache in cmDefaultMap.values) {
            cache.shutdown()
        }
    }


}


package com.IceCreamQAQ.Yu.cache

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.BeanFactory
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element

@NotSearch
class EhcacheHelp<T>(private val cache: Cache) {

    operator fun get(key: String): T? {
        return cache.get(key)?.objectValue as? T?
    }

    operator fun set(key: String,value: T){
        cache.put(Element(key, value))
    }

    fun remove(key:String){
        cache.remove(key)
    }

}

class EhcacheHelpFactory : BeanFactory<EhcacheHelp<*>>,ApplicationService {

    @Config("yu.cache.ehcache.config")
    @Default("ehcache-yu-default.xml")
    private lateinit var ehcacheConfigLocation:String
    private lateinit var cm: CacheManager

    override fun createBean(clazz: Class<EhcacheHelp<*>>, name: String): EhcacheHelp<*> {
        return EhcacheHelp<Any>(cm.getCache(name))
    }

    override fun init() {
        val url = javaClass.classLoader.getResource(ehcacheConfigLocation)
        cm = CacheManager.newInstance(url)
    }

    override fun start() {

    }

    override fun stop() {
        cm.shutdown()
    }
}


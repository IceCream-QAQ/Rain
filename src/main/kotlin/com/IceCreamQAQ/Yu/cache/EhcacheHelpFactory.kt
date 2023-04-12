package com.IceCreamQAQ.Yu.cache

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.config.ConfigManager
import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import java.time.Duration
import javax.inject.Inject


class EhcacheHelpFactory(
    configManager: ConfigManager
) : BeanFactory<EhcacheHelp<*>>, ApplicationService {

    private var cm: CacheManager? = null

    override val type = EhcacheHelp::class.java

    init {
        var cmb = CacheManagerBuilder.newCacheManagerBuilder()
        val config = configManager.getMap("yu.cache.ehcaches", EhcacheConfig::class.java)
        config.forEach { (name, cc) ->
            cmb = cmb.withCache(
                name,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String::class.java,
                    Any::class.java,
                    ResourcePoolsBuilder.heap(cc.size)
                ).apply {
                    if (cc.ttl > 0)
                        withExpiry(
                            ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(cc.ttl))
                        )

                    if (cc.tti > 0)
                        withExpiry(
                            ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(cc.tti))
                        )
                }.build()
            )
        }

        cm = cmb.build(true)
    }

    override fun createBean(name: String): EhcacheHelp<*> {
        return EhcacheHelp<Any>(cm?.getCache(name, String::class.java, Any::class.java) ?: error("Cache $name not found!"))
    }

    override fun init() {
    }

    override fun start() {

    }

    override fun stop() {
        cm?.close()
    }


}
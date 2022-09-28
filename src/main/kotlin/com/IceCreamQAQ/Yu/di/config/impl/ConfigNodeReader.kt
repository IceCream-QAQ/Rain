package com.IceCreamQAQ.Yu.di.config.impl

import com.IceCreamQAQ.Yu.di.config.ConfigManager
import com.IceCreamQAQ.Yu.di.config.ConfigReader
import com.IceCreamQAQ.Yu.util.type.RelType

class ConfigNodeReader<T>(val config: ConfigManager, val name: String, val type: RelType<T>) : ConfigReader<T> {
    override fun invoke(): T? = config.getConfig(name, type)

    override fun invoke(name: String): T? = config.getConfig(name, type)
}
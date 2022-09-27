package com.IceCreamQAQ.Yu.di.config.impl

import com.IceCreamQAQ.Yu.di.config.ConfigManager
import com.IceCreamQAQ.Yu.di.config.ConfigReader
import com.IceCreamQAQ.Yu.util.type.RelType

class ConfigArrayReader<T>(val config: ConfigManager, val name: String, val type: RelType<*>) : ConfigReader<T> {
    override fun invoke(): T = config.getArray(name, type) as T

    override fun invoke(name: String): T = config.getArray(name, type) as T
}
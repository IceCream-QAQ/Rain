package com.IceCreamQAQ.Yu.di.config.impl

import com.IceCreamQAQ.Yu.di.config.ConfigManager
import com.IceCreamQAQ.Yu.di.config.ConfigReader

class ConfigImpl(
    val classLoader: ClassLoader,
    val runMode: String?,
    val launchPackage: String
) : ConfigManager {
    override fun <T> getConfigReader(): ConfigReader<T> {
        TODO("Not yet implemented")
    }
}
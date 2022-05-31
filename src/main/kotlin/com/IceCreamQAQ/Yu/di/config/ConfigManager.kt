package com.IceCreamQAQ.Yu.di.config

import com.IceCreamQAQ.Yu.di.ConfigReader

interface ConfigManager {

    fun <T> getConfigReader(): ConfigReader<T>

}
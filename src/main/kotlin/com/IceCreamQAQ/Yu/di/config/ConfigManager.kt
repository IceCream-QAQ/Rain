package com.IceCreamQAQ.Yu.di.config

interface ConfigManager {

    fun <T> getConfigReader(): ConfigReader<T>

}
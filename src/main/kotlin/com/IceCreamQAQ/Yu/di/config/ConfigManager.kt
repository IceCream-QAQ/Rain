package com.IceCreamQAQ.Yu.di.config

import java.lang.reflect.Type

interface ConfigManager {

    fun <T> getConfigReader(type: Type): ConfigReader<T>
    fun <T> getConfigWriter(type: Type): ConfigReader<T>

}
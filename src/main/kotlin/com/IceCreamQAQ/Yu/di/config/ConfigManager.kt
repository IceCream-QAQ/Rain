package com.IceCreamQAQ.Yu.di.config

import com.IceCreamQAQ.Yu.util.type.RelType

interface ConfigManager {

    companion object {
        inline fun <reified T> ConfigManager.getConfig(name: String): T? =
            this.getConfig(name, RelType.create(T::class.java))

        inline fun <reified T> ConfigManager.getArray(name: String): List<T> =
            this.getArray(name, RelType.create(T::class.java))
    }

    fun <T> getConfig(name: String, type: Class<T>): T? =
        this.getConfig(name, RelType.create(type))

    fun <T> getArray(name: String, type: Class<T>): List<T> =
        this.getArray(name, RelType.create(type))

    fun <T> getConfig(name: String, type: RelType<T>): T?
    fun <T> getArray(name: String, type: RelType<T>): List<T>
    fun <T> getConfigReader(name: String, type: RelType<T>): ConfigReader<T>
    fun <T> getConfigWriter(name: String, type: RelType<T>): ConfigReader<T>

}
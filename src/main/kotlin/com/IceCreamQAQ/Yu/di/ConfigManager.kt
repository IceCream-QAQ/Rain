package com.IceCreamQAQ.Yu.di

interface ConfigManager {

    companion object {

        inline fun <reified T> ConfigManager.get(key: String): T? =
            get(key, T::class.java)

        inline fun <reified T> ConfigManager.array(key: String): List<T> =
            getArray(key, T::class.java)
    }

    fun <T> get(key: String, type: Class<T>): T?
    fun <T> getArray(key: String, type: Class<T>): List<T>

}


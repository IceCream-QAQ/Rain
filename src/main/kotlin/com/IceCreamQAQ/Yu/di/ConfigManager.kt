package com.IceCreamQAQ.Yu.di

interface ConfigManager {

    fun <T> get(key: String, type: Class<T>): T?
    fun <T> getArray(key: String, type: Class<T>): MutableList<T>?

}
package com.IceCreamQAQ.Yu.di

import com.alibaba.fastjson.JSONObject

interface ConfigManager {

    fun <T> get(key: String, type: Class<T>): T?
    fun <T> getArray(key: String, type: Class<T>): MutableList<T>?

}
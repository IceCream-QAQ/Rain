package com.IceCreamQAQ.Yu.controller

interface PathManager {
    fun getPath(clazz: Class<*>, instance: Any): String?
}
package com.IceCreamQAQ.Yu.controller.old

import java.lang.reflect.Method

interface PathManager {
    fun getPath(clazz: Class<*>, instance: Any): String?
}

interface ActionManager {
    fun getPath(clazz: Class<*>, method: Method, instance: Any): String?
}

interface SynonymManager {
    fun getPath(clazz: Class<*>, method: Method, instance: Any): Array<String>?
}


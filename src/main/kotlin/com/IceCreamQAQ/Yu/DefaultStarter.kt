package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.loader.AppClassloader

class DefaultStarter {

    companion object{

        @JvmStatic
        fun start(){
            val appClassLoader = AppClassloader(DefaultStarter::class.java.classLoader)

            val appClass = appClassLoader.loadClass("com.IceCreamQAQ.Yu.DefaultApp")
            val startMethod = appClass.getMethod("start")

            val app = appClass.newInstance()
            startMethod.invoke(app)
        }

    }

}
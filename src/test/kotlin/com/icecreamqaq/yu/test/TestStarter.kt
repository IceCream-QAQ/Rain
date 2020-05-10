package com.icecreamqaq.yu.test

import com.IceCreamQAQ.Yu.loader.AppClassloader

class TestStarter {

    companion object{

        @JvmStatic
        fun start(){
            val appClassLoader = AppClassloader(TestStarter::class.java.classLoader)

            val appClass = appClassLoader.loadClass("com.icecreamqaq.yu.test.TestApp")
            val startMethod = appClass.getMethod("start")

            val app = appClass.newInstance()
            startMethod.invoke(app)

            val testMethod = appClass.getMethod("test")
            testMethod.invoke(app)
        }

    }

}
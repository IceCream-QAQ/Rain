package com.icecreamqaq.test.yu

import com.IceCreamQAQ.Yu.DefaultStarter
import com.IceCreamQAQ.Yu.loader.AppClassloader

class TestStarter {

    companion object{

        @JvmStatic
        fun start(args:Array<String>){
            DefaultStarter.init(args)

            val appClassLoader = AppClassloader(TestStarter::class.java.classLoader)

            val appClass = appClassLoader.loadClass("com.icecreamqaq.test.yu.TestApp")
            val startMethod = appClass.getMethod("start")

            val app = appClass.newInstance()
            startMethod.invoke(app)

            val testMethod = appClass.getMethod("test")
            testMethod.invoke(app)
        }

    }

}
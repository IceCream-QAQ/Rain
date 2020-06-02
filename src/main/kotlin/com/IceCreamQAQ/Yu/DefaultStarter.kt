package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.loader.AppClassloader

class DefaultStarter {

    companion object {

        @JvmStatic
        fun init(args: Array<String>){
            var runMode: String? = null
            for (i in args.indices) {
                val arg = args[i]
                if (arg == "-runMode") {
                    val ii =i + 1
                    if (args.size >= ii)
                        runMode = args[ii]
                }
            }

            if (runMode != null) System.setProperty("yu-runMode", runMode)
        }

        @JvmStatic
        fun start() {
            val appClassLoader = AppClassloader(DefaultStarter::class.java.classLoader)

            val appClass = appClassLoader.loadClass("com.IceCreamQAQ.Yu.DefaultApp")
            val startMethod = appClass.getMethod("start")

            val app = appClass.newInstance()
            startMethod.invoke(app)
        }

    }

}
package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.hook.IHook
import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer

object FullStackApplicationLauncher {

    @JvmStatic
    fun launch(args: Array<String>) {
        args.iterator().apply {
            while (hasNext())
                when (next()) {
                    "-runMode" -> if (hasNext()) System.setProperty("yu.runMode", next())
                }
        }

        launch()
    }

    @JvmStatic
    fun launch() {
//        Class.forName(Thread.currentThread().stackTrace.last().className).apply {
//            if (!hasAnnotation<NotSearch>())
//                System.setProperty("yu.launchPackage", name.substring(name.lastIndexOf(".")))
//        }
        val appClassLoader = AppClassloader(Application::class.java.classLoader)
        Thread.currentThread().contextClassLoader = appClassLoader

        // 初始化 EnchantManager
        appClassLoader.apply {
            registerBlackClass("com.IceCreamQAQ.Yu.annotation.EnchantBy")
            registerBlackClass("com.IceCreamQAQ.Yu.loader.enchant.Enchanter")

            registerTransformer(
                loadClass("com.IceCreamQAQ.Yu.loader.enchant.EnchantManager").run {
                    getConstructor(IRainClassLoader::class.java).newInstance(appClassLoader)
                } as ClassTransformer
            )
        }


        // 初始化 YuHook
        appClassLoader.apply {
            registerBlackClass("com.IceCreamQAQ.Yu.annotation.HookBy")
            registerBlackClass("com.IceCreamQAQ.Yu.annotation.InstanceMode")

            registerBlackClass("com.IceCreamQAQ.Yu.hook.IHook")
            registerBlackClass("com.IceCreamQAQ.Yu.hook.HookRunnable")
            registerBlackClass("com.IceCreamQAQ.Yu.hook.HookItem")
            registerBlackClass("com.IceCreamQAQ.Yu.hook.HookInfo")

            registerTransformer(
                loadClass("com.IceCreamQAQ.Yu.hook.HookImpl")
                    .getConstructor(IRainClassLoader::class.java, IHook::class.java)
                    .newInstance(this, null) as ClassTransformer
            )
        }


        val applicationClass = appClassLoader.loadClass("com.IceCreamQAQ.Yu.Application")
        applicationClass.getMethod("start").invoke(applicationClass.newInstance())
    }

}
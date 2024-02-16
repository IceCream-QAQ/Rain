package rain.application

import rain.classloader.AppClassloader
import rain.classloader.IRainClassLoader
import rain.classloader.transformer.ClassTransformer
import rain.function.getCallPackage
import rain.function.getCaller
import rain.hook.IHook

object FullStackApplicationLauncher {

    @JvmStatic
    @JvmOverloads
    fun launch(callPackage: String = getCallPackage()) {
//        Class.forName(Thread.currentThread().stackTrace.last().className).apply {
//            if (!hasAnnotation<NotSearch>())
//                System.setProperty("yu.launchPackage", name.substring(name.lastIndexOf(".")))
//        }
        if (callPackage.isNotEmpty()) System.setProperty("rain.launchPackage", callPackage)
        val appClassLoader = AppClassloader(Application::class.java.classLoader)
        Thread.currentThread().contextClassLoader = appClassLoader

        // 初始化 EnchantManager
        appClassLoader.apply {
            registerBlackClass("rain.classloader.enchant.EnchantBy")
            registerBlackClass("rain.classloader.enchant.Enchanter")

            registerTransformer(
                loadClass("rain.classloader.enchant.EnchantManager").run {
                    getConstructor(IRainClassLoader::class.java).newInstance(appClassLoader)
                } as ClassTransformer
            )
        }

        // 注入参数验证方法 ValidHook 注解添加 Transformer
//        appClassLoader.apply {
//            registerTransformer(
//                loadClass("com.IceCreamQAQ.Yu.validation.global.ValidHookAddTransformer").run {
//                    getConstructor(IRainClassLoader::class.java).newInstance(appClassLoader)
//                } as ClassTransformer
//            )
//        }

        // 初始化 YuHook
        appClassLoader.apply {
            registerBlackClass("rain.hook.HookBy")
            registerBlackClass("rain.hook.InstanceMode")

            registerBlackClass("rain.hook.IHook")
            registerBlackClass("rain.hook.HookRunnable")
            registerBlackClass("rain.hook.HookItem")
            registerBlackClass("rain.hook.HookInfo")
            registerBlackClass("rain.hook.HookContext")

            registerTransformer(
                loadClass("rain.hook.HookImpl")
                    .getConstructor(IRainClassLoader::class.java, IHook::class.java)
                    .newInstance(this, null) as ClassTransformer
            )
        }

        // 注册 Kotlin ByInject 实现
//        appClassLoader.registerTransformer("com.IceCreamQAQ.Yu.di.kotlin.YuContextKotlinInjectTransformer")


        val applicationClass = appClassLoader.loadClass("rain.application.Application")
        applicationClass.getMethod("start").invoke(applicationClass.newInstance())
    }

}
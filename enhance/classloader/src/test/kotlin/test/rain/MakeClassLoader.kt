package test.rain

import rain.classloader.AppClassloader
import rain.classloader.IRainClassLoader
import rain.classloader.transformer.ClassTransformer
import rain.function.castNotNull

object MakeClassLoader {

    fun makeClassLoader() = AppClassloader(MakeClassLoader::class.java.classLoader)

    fun enableEnchant(classloader: AppClassloader){
        classloader.apply {
            registerBlackClass("rain.classloader.enchant.EnchantBy")
            registerBlackClass("rain.classloader.enchant.Enchanter")

            loadClass("rain.classloader.enchant.EnchantManager")
                .getConstructor(IRainClassLoader::class.java)
                .newInstance(this)
                .castNotNull<ClassTransformer>()
                .let { registerTransformer(it) }
        }
    }

    fun classloader(): ClassLoader {
        val appClassLoader = makeClassLoader()
        Thread.currentThread().contextClassLoader = appClassLoader
        enableEnchant(appClassLoader)
        return appClassLoader
    }
}
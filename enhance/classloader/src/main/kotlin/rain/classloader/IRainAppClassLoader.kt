package rain.classloader

import rain.classloader.transformer.ClassTransformer


interface IRainAppClassLoader : IRainClassLoader {

    val superApp: IRainAppClassLoader?

    fun registerTransformer(className: String)
    fun registerTransformer(transformer: ClassTransformer)
    fun registerBlackClass(className: String)
    fun registerBlackPackage(packageName: String)
    fun registerWhiteClass(className: String)
    fun registerWhitePackage(packageName: String)

}
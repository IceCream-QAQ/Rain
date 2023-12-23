package rain.classloader

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import rain.classloader.transformer.ClassTransformer
import rain.function.IO
import rain.function.IO.Companion.tmpLocation
import rain.function.newFolder
import rain.function.slf4j
import java.io.File

class AppClassloader(parent: ClassLoader) : ClassLoader(parent), IRainAppClassLoader {

    companion object {
        private val log = slf4j()

        private var classOutLocation = newFolder(tmpLocation, "classOutput")

        private val transformerList: MutableList<String> = ArrayList()

        @JvmStatic
        fun registerTransformerList(className: String) {
            transformerList.add(className)
        }

        private val blackList: MutableList<String> = ArrayList()

        @JvmStatic
        fun registerBackList(packageName: List<String>?) {
            blackList.addAll(packageName!!)
        }

    }

    override val superApp: IRainAppClassLoader? = null

    private val transformers: MutableList<ClassTransformer> = ArrayList()
    private val blackPackages: MutableList<String> = ArrayList()
    private val whitePackages: MutableList<String> = ArrayList()

    private val blackClasses: MutableList<String> = ArrayList()
    private val whiteClasses: MutableList<String> = ArrayList()

    init {
        for (s in transformerList) {
            transformers.add(loadClass(s, true, false).newInstance() as ClassTransformer)
        }

        blackPackages.addAll(blackList)

        blackClasses.apply {
            add("com.IceCreamQAQ.Yu.loader.IRainClassLoader")
            add("rain.classloader.IRainAppClassLoader")
            add("com.IceCreamQAQ.Yu.loader.AppClassloader")

            add("com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer")
        }
    }

    override fun registerTransformer(className: String) {
        transformers.add(loadClass(className, true, false).newInstance() as ClassTransformer)
    }

    override fun registerTransformer(transformer: ClassTransformer) {
        transformers.add(transformer)
    }

    override fun registerBlackClass(className: String) {
        blackClasses.add(className)
    }

    override fun registerBlackPackage(packageName: String) {
        blackPackages.add(packageName)
    }

    override fun registerWhiteClass(className: String) {
        whiteClasses.add(className)
    }

    override fun registerWhitePackage(packageName: String) {
        whitePackages.add(packageName)
    }

    public override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return loadClass(name, resolve, true)
    }

    fun loadClass(name: String, resolve: Boolean, enhance: Boolean): Class<*> {
        var c = findLoadedClass(name)
        if (c != null) {
            return c
        }
        if (
            name !in whiteClasses &&
            !inWhitePackages(name) &&
            (name in blackClasses || inBlackPackages(name))
        ) c = parent.loadClass(name)

        try {
            if (c == null) if (enhance) c = loadAppClass(name, resolve)
        } catch (e: ClassNotFoundException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("加载类: $name 出错！", e)
        }
        if (null == c) c = super.loadClass(name, resolve)
        name.lastIndexOf(".").let {
            if (it != -1){
                val pkgName = name.substring(0, it)
                if (getPackage(pkgName) == null) {
                    try {
                        definePackage(pkgName, null, null, null, null, null, null, null)
                    } catch (iae: IllegalArgumentException) {
                        throw AssertionError("Cannot find package " + pkgName)
                    }
                }
            }
        }

        return c
    }

    private fun loadAppClass(name: String, resolve: Boolean): Class<*> {
        log.trace(String.format("Load Class: %s.", name))
        val path = name.replace(".", "/") + ".class"
        val input = parent.getResourceAsStream(path) ?: throw ClassNotFoundException(name)
        var changed = false
        var bytes = input.use { it.readBytes() }
        val reader = ClassReader(bytes)
        val node = ClassNode()
        reader.accept(node, 0)
        for (transformer in transformers) {
            if (transformer.transform(node, name)) changed = true
        }
        if (changed) {
            val ncw = ClassWriter(0)
            node.accept(ncw)
            bytes = ncw.toByteArray()
            IO.writeFile(File(classOutLocation, "$name.class"), bytes)
        }
        val c = defineClass(name, bytes, 0, bytes.size)
        if (resolve) resolveClass(c)
        return c
    }

    override fun define(name: String, data: ByteArray): Class<*> {
        return defineClass(name, data, 0, data.size)
    }

    private fun inBlackPackages(name: String): Boolean {
        val b = (name.startsWith("java.")
                || name.startsWith("jdk.")
                || name.startsWith("javax.")
                || name.startsWith("kotlin.")
                || name.startsWith("kotlinx.")
                || name.startsWith("org.objectweb.asm.")
                || name.startsWith("com.google.")
                || name.startsWith("org.apache.")
                || name.startsWith("org.w3c.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.")
                || name.startsWith("ch.qos.logback.core.")
                || name.startsWith("org.xml.")
                || name.startsWith("org.slf4j.")
                || name.startsWith("org.jboss."))
        if (b) return true
        for (s in blackPackages) {
            if (name.startsWith(s)) return true
        }
        return false
    }

    private fun inWhitePackages(name: String): Boolean {
        for (s in whitePackages) {
            if (name.startsWith(s)) return true
        }
        return false
    }

    override fun forName(name: String, initialize: Boolean): Class<*> {
        return loadClass(name, initialize)
    }


}
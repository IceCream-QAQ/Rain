package com.IceCreamQAQ.Yu.loader

import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer
import com.IceCreamQAQ.Yu.slf4j
import com.IceCreamQAQ.Yu.util.IO.Companion.read
import com.IceCreamQAQ.Yu.util.IO.Companion.tmpLocation
import com.IceCreamQAQ.Yu.util.IO.Companion.writeFile
import com.IceCreamQAQ.Yu.util.newFolder
import lombok.SneakyThrows
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.IOException

class AppClassloader(parent: ClassLoader) : ClassLoader(parent), IRainClassLoader {

    companion object {
        private val log = slf4j<AppClassloader>()

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

    private val transformers: MutableList<ClassTransformer> = ArrayList()
    private val blackPackages: MutableList<String> = ArrayList()
    private val whitePackages: MutableList<String> = ArrayList()

    init {
        for (s in transformerList) {
            transformers.add(loadClass(s, true, false).newInstance() as ClassTransformer)
        }

        blackList.addAll(blackList)
    }

    fun registerTransformer(className: String) {
        transformers.add(loadClass(className, true, false).newInstance() as ClassTransformer)
    }

    fun registerTransformer(transformer: ClassTransformer) {
        transformers.add(transformer)
    }

    public override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return loadClass(name, resolve, true)
    }

    fun loadClass(name: String, resolve: Boolean, enhance: Boolean): Class<*> {
        var c = findLoadedClass(name)
        if (c != null) {
            return c
        }
        if (!inWhitePackages(name) && inBlackPackages(name)) c = parent.loadClass(name)

        try {
            if (c == null) if (enhance) c = loadAppClass(name, resolve)
        } catch (e: ClassNotFoundException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("加载类: $name 出错！", e)
        }
        if (null == c) c = super.loadClass(name, resolve)
        val pkgName = name.substring(0, name.lastIndexOf("."))
        //        if (getParent())
        if (getPackage(pkgName) == null) {
            try {
                definePackage(pkgName, null, null, null, null, null, null, null)
            } catch (iae: IllegalArgumentException) {
                throw AssertionError("Cannot find package " + pkgName)
            }
        }
        return c
    }

    private fun loadAppClass(name: String, resolve: Boolean): Class<*> {
        log.trace(String.format("Load Class: %s.", name))
        val path = name.replace(".", "/") + ".class"
        val input = parent.getResourceAsStream(path) ?: throw ClassNotFoundException(name)
        var changed = false
        var bytes = read(input, true)
        val reader = ClassReader(bytes)
        val node = ClassNode()
        reader.accept(node, 0)
        for (transformer in transformers) {
            if (transformer.transform(node, name)) changed = true
        }
        if (changed) {
            val ncw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            node.accept(ncw)
            bytes = ncw.toByteArray()
            writeFile(File(classOutLocation, "$name.class"), bytes)
        }
        val c = defineClass(name, bytes, 0, bytes.size)
        if (resolve) resolveClass(c)
        return c
    }

    override fun define(name: String, data: ByteArray): Class<*> {
        return defineClass(name, data, 0, data.size)
    }

    override fun getPackage(name: String): Package? {
        return super.getPackage(name)
    }

    private fun inBlackPackages(name: String): Boolean {
        val b = (name.startsWith("java.")
                || name.startsWith("jdk.")
                || name.startsWith("javax.")
                || name.startsWith("kotlin")
                || name.startsWith("com.google.")
                || name.startsWith("org.apache.")
                || name.startsWith("org.w3c.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.")
                || name.startsWith("net.sf.ehcache")
                || name.startsWith("com.IceCreamQAQ.Yu.annotation.")
                || name.startsWith("com.IceCreamQAQ.Yu.hook.")
                || name.startsWith("com.IceCreamQAQ.Yu.loader.enchant.")
                || name.startsWith("com.IceCreamQAQ.Yu.loader.AppClassloader")
                || name.startsWith("ch.qos.logback.core.")
                || name.startsWith("org.xml")
                || name.startsWith("org.slf4j.")
                || name.startsWith("org.jboss"))
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

    @SneakyThrows
    override fun forName(name: String, initialize: Boolean): Class<*> {
        return loadClass(name, initialize)
    }


}
package rain.classloader

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.slf4j.LoggerFactory
import rain.classloader.transformer.ClassTransformer

open class BasicRainClassLoader(
    val parentApp: BasicRainClassLoader?,
    parentClassloader: ClassLoader? = parentApp
) : ClassLoader(parentClassloader), IRainClassLoader {

    companion object {
        private val log = LoggerFactory.getLogger(BasicRainClassLoader::class.java)
    }

    val transformers: MutableList<ClassTransformer> = ArrayList()
    val blackPackages: MutableList<String> = ArrayList()
    val whitePackages: MutableList<String> = ArrayList()

    val blackClasses: MutableList<String> = ArrayList()
    val whiteClasses: MutableList<String> = ArrayList()

    open fun transform(node: ClassNode, className: String): Boolean {
        var update = parentApp?.transform(node, className) ?: false

        transformers.forEach {
            if (it.transform(node, className)) update = true
        }
        return update
    }

    public override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return loadClass(name, resolve, true).apply { loadPackage(name) }
    }

    open fun loadClass(name: String, resolve: Boolean, enhance: Boolean): Class<*> {
        findLoadedClass(name)?.let { return it }

        if (name !in whiteClasses && !inWhitePackages(name))
            parentApp?.loadClass(name, resolve)?.let { return it }

        if (inBlackPackages(name) || name in blackClasses)
            return parentApp?.loadClass(name, resolve) ?: super.loadClass(name, resolve)

        try {
            return doLoadClass(name, resolve)
        } catch (e: ClassNotFoundException) {
            throw e
        } catch (e: Throwable) {
            throw RuntimeException("加载类: $name 出错！", e)
        }
    }

    open fun loadPackage(name: String) {
        name.lastIndexOf(".")
            .let { if (it != -1) name.substring(0, it) else null }
            ?.runCatching {
                getPackage(this)
                    ?: definePackage(this, null, null, null, null, null, null, null)
            }?.getOrElse { throw AssertionError("Cannot find package: $name.", it) }
    }

    open fun doLoadClass(name: String, resolve: Boolean): Class<*> {
        log.trace(String.format("Load Class: %s.", name))
        val path = name.replace(".", "/") + ".class"
        val input = parent.getResourceAsStream(path) ?: throw ClassNotFoundException(name)
        var bytes = input.use { it.readBytes() }
        val reader = ClassReader(bytes)
        val node = ClassNode()
        reader.accept(node, 0)
        val changed = transform(node, name)
        if (changed) {
            val ncw = ClassWriter(0)
            node.accept(ncw)
            bytes = ncw.toByteArray()
//            IO.writeFile(File(classOutLocation, "$name.class"), bytes)
        }
        val c = defineClass(name, bytes, 0, bytes.size)
        if (resolve) resolveClass(c)
        return c
    }

    override fun define(name: String, data: ByteArray): Class<*> {
        return defineClass(name, data, 0, data.size)
    }

    override fun forName(name: String, initialize: Boolean): Class<*> {
        return loadClass(name, initialize)
    }

    open fun inBlackPackages(name: String): Boolean {
        val b = (name.startsWith("java.")
                || name.startsWith("jdk.")
                || name.startsWith("javax.")
                || name.startsWith("kotlin.")
                || name.startsWith("kotlinx.")
                || name.startsWith("org.objectweb.asm.")
                || name.startsWith("org.w3c.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.")
                || name.startsWith("org.slf4j.")
                || name.startsWith("org.junit."))

        if (b) return true
        for (s in blackPackages) {
            if (name.startsWith(s)) return true
        }
        return false
    }

    open fun inWhitePackages(name: String): Boolean {
        for (s in whitePackages) {
            if (name.startsWith(s)) return true
        }
        return false
    }

}
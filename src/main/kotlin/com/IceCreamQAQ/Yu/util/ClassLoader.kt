package com.IceCreamQAQ.Yu.util

import com.IceCreamQAQ.Yu.annotation.NotSearch
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

fun ClassLoader.findClassByPackage(packageName: String): Set<Class<*>> =
    getClasses(packageName)

private fun ClassLoader.getClasses(packageName: String): Set<Class<*>> {
    val classes = HashSet<Class<*>>()
    val packageDirName = packageName.replace('.', '/')
    val dirs = getResources(packageDirName)
    while (dirs.hasMoreElements()) {
        val url = dirs.nextElement()
        when (url.protocol) {
            "file" -> {
                val filePath = URLDecoder.decode(url.file, "UTF-8")
                findAndAddClassesInPackageByFile(packageName, filePath, classes)
            }
            "jar" -> {
                val jar = (url.openConnection() as JarURLConnection).jarFile
                jar.stream().map { entry ->
                    val name = entry.name.let { if (it[0] == '/') it.substring(1) else it }!!
                    if (name.startsWith(packageDirName))
                        if (name.endsWith(".class") && !entry.isDirectory)
                            name.substring(packageName.length + 1, name.length - 6)
                                .let { classes.add(loadClass("$packageName.$it")) }
                }
            }
        }
    }
    return classes
}

private fun ClassLoader.findAndAddClassesInPackageByFile(
    packageName: String,
    packagePath: String,
    classes: HashSet<Class<*>>
) {
    val dir = File(packagePath)
    if (!dir.exists() || !dir.isDirectory) return
    dir.listFiles()?.map { it: File ->
        if (it.isDirectory) findAndAddClassesInPackageByFile(packageName + "." + it.name, it.absolutePath, classes)
        else if (it.name.endsWith(".class"))
            it.name.subStringByLast(6).let { classes.add(loadClass("$packageName.$it")) }
    }
}
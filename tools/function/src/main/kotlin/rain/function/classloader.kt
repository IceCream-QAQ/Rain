package rain.function

import java.io.File
import java.net.JarURLConnection
import java.net.URLDecoder
import java.util.*

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

            "jar" ->
                (url.openConnection() as JarURLConnection).jarFile.entries().iterator().forEach { entry ->
                    val name = entry.name.let { if (it[0] == '/') it.substring(1) else it }!!
                    if (name.startsWith(packageDirName))
                        if (name.endsWith(".class") && !entry.isDirectory)
                            name.subStringByLast(6).replace("/", ".")
                                .let { classes.add(loadClass(it)) }
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
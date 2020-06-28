package com.IceCreamQAQ.Yu.loader

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.hook.YuHook
import com.IceCreamQAQ.Yu.isBean
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AppLoader {

    @Inject
    lateinit var context: YuContext

    @Inject
    lateinit var logger: AppLogger

    @Inject
    @field:Named("appClassLoader")
    lateinit var appClassloader: ClassLoader

    @Config("yu.scanPackages")
    lateinit var scanPackages: List<String>

    @Config("yu.classRegister")
    var classRegister: List<String> = listOf()

    fun load() {
        try {
            val rsList = ArrayList<ClassRegister>(classRegister.size)
            for (rs in classRegister) {
                rsList.add(context.getBean(rs, "") as ClassRegister?
                        ?: error("Can't Create ClassRegister: $rs Instance."))
            }
            val classes = HashMap<String, Class<*>>()
            for (s in scanPackages) {
                classes.putAll(getClasses(s))
            }
            val loadItemsMap = HashMap<Class<out Loader>, MutableMap<String, MutableMap<String, LoadItem>>>()

            for (clazz in classes.values) {
                for (register in rsList) {
                    register.register(clazz)
                }
                searchLoadBy(clazz, clazz, loadItemsMap)
            }

//            val beanFactories = loadItemsMap[BeanFactoryLoader::class.java] ?: HashMap()
//            val bfl = context.newBean(BeanFactoryLoader::class.java) ?: throw BeanCreateError("Cant Instanced Loader!")
//            bfl.load(beanFactories)
//            loadItemsMap.remove(BeanFactoryLoader::class.java)
            val loaders = ArrayList<Loader>()

            for (loader in loadItemsMap.keys) {
                loaders.add(context[loader] ?: continue)
//                loaderInstance.load(loadItemsMap[loader] ?: continue)
            }

            for (i in 0 until loaders.size) {
                for (j in 0 until loaders.size - 1 - i) {
                    val c = loaders[j]
                    val n = loaders[j + 1]
                    if (c.width() > n.width()) {
                        loaders[j] = n
                        loaders[j + 1] = c
                    }
                }
            }

            for (loader in loaders) {
                val ls = loadItemsMap[loader::class.java] ?: continue
                for (l in ls.values) {
                    loader.load(l)
                }
            }

//            for (clazz in classes.values) {
//                context[clazz]
//            }

            for (hook in YuHook.getRunnables()) {
                context.injectBean(hook)
            }
        } catch (e: Exception) {
            throw RuntimeException("程序初始化失败！", e)
        }
    }

    fun searchLoadBy(loadClass: Class<*>, searchClass: Class<*>, loadItemsMap: HashMap<Class<out Loader>, MutableMap<String, MutableMap<String, LoadItem>>>) {
        if (!loadClass.isBean()) return
        val loadBy = searchClass.getAnnotation(LoadBy::class.java)
        if (loadBy != null) {
            addLoadItem(loadClass, loadBy, loadBy, loadItemsMap)
        }

        val annotationInstances = searchClass.annotations
        for (annotationInstance in annotationInstances) {
            val annotationClass = annotationInstance::class.java.interfaces[0]
            addLoadItem(loadClass, annotationInstance, annotationClass.getAnnotation(LoadBy::class.java)
                    ?: continue, loadItemsMap)
//            return
        }

        val superClass = searchClass.superclass
        if (superClass != null) searchLoadBy(loadClass, superClass, loadItemsMap)

        val interfaces = searchClass.interfaces
        for (i in interfaces) {
            searchLoadBy(loadClass, i, loadItemsMap)
        }
    }

    fun addLoadItem(loadClass: Class<*>, annotationInstance: Annotation, loadBy: LoadBy, loadItemsMap: HashMap<Class<out Loader>, MutableMap<String, MutableMap<String, LoadItem>>>) {
        val loader = Class.forName(loadBy.value.java.name) as Class<out Loader>
        val loadItems = loadItemsMap[loader] ?: {
            val l = HashMap<String, MutableMap<String, LoadItem>>()
            loadItemsMap[loader] = l
            l
        }()
        val lii = loadItems[annotationInstance::class.java.name] ?: {
            val l = HashMap<String, LoadItem>()
            loadItems[annotationInstance::class.java.name] = l
            l
        }()
        val loadItem = LoadItem()
        loadItem.loadBy = annotationInstance
        loadItem.type = loadClass
        lii[loadClass.name] = loadItem
    }

    @Throws(MalformedURLException::class)
    fun getClasses(pn: String): Map<String, Class<*>> {
        var packageName = pn
        val classes = HashMap<String, Class<*>>()
        //List<Class<?>> classes = new ArrayList<>();
        val recursive = true
        val packageDirName = packageName.replace('.', '/')
        val dirs: Enumeration<URL>
        try {
            dirs = appClassloader.getResources(packageDirName)
            while (dirs.hasMoreElements()) {
                val url = dirs.nextElement()

                val protocol = url.protocol
                if ("file" == protocol) {
                    val filePath = URLDecoder.decode(url.file, "UTF-8")
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes)
                } else if ("jar" == protocol) {
                    var jar: JarFile
                    try {
                        jar = (url.openConnection() as JarURLConnection).jarFile
                        val entries = jar.entries()
                        while (entries.hasMoreElements()) {
                            val entry = entries.nextElement()
                            var name = entry.name
                            if (name[0] == '/') {
                                name = name.substring(1)
                            }
                            if (name.startsWith(packageDirName)) {
                                val idx = name.lastIndexOf('/')
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.')
                                }
                                if (idx != -1 || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory) {
                                        val className = name.substring(packageName.length + 1, name.length - 6)
                                        try {
                                            val clazz = appClassloader.loadClass("$packageName.$className")
                                            if (clazz.getAnnotation(NotSearch::class.java) == null)
                                                classes.putIfAbsent(clazz.name, clazz)
                                        } catch (e: ClassNotFoundException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return classes
    }

    fun findAndAddClassesInPackageByFile(packageName: String, packagePath: String?, recursive: Boolean, classes: MutableMap<String, Class<*>>) {
        val dir = File(packagePath)
        if (!dir.exists() || !dir.isDirectory) {
            return
        }
        val dirfiles = dir.listFiles { file: File -> recursive && file.isDirectory || file.name.endsWith(".class") }
        for (file in dirfiles) {
            if (file.isDirectory) {
                findAndAddClassesInPackageByFile(packageName + "." + file.name, file.absolutePath, recursive, classes)
            } else {
                val className = file.name.substring(0, file.name.length - 6)
                try {
                    val clazz = Class.forName("$packageName.$className")
                    if (clazz.getAnnotation(NotSearch::class.java) == null)
                        classes.putIfAbsent(clazz.name, clazz)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }


}
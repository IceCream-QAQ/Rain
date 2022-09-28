package com.IceCreamQAQ.Yu.loader

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.ClassContext
import com.IceCreamQAQ.Yu.di.impl.BeanFactoryClassContext
import com.IceCreamQAQ.Yu.di.impl.BindableClassContext.Companion.putBinds
import com.IceCreamQAQ.Yu.di.impl.ContextImpl
import com.IceCreamQAQ.Yu.di.impl.NoInstanceClassContext
import com.IceCreamQAQ.Yu.di.isBean
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.mapMap
import com.IceCreamQAQ.Yu.named
import com.IceCreamQAQ.Yu.util.findClassByPackage
import com.IceCreamQAQ.Yu.util.getOrPut
import com.IceCreamQAQ.Yu.util.type.RelType
import javax.inject.Inject
import javax.inject.Named

open class AppLoader(
    @Named("appClassloader")
    open val classLoader: ClassLoader,
    open val context: ContextImpl,
    @Config("yu.scanPackages")
    open val scanPackages: List<String>,
    @Config("yu.classRegisters")
    open val classRegister: List<String>,
    @Config("yu.modules")
    open val modules: List<String>
) {

    open fun load() {
        modules.forEach {
            context.getBean(Class.forName(it, true, classLoader) as Class<out Module>)!!.onLoad()
        }

        val classes = HashSet<Class<*>>()
        scanPackages.forEach { classes.addAll(classLoader.findClassByPackage(it)) }

        /***
         * 数据结构
         * Map<Loader, Map<Class(标记方式), Map<Class(被扫描类), LoadItem>>>
         * 一个类上出现多种不同的标记方式指向同一个 Loader，则可以为每个标记方式，分别加载一次该类。
         * 如果一个类通过标记一个标记了 LoadBy 注解的注解，则标记方式为注解。
         * 如果一个类通过继承一个类/实现一个接口，指向一个标记 LoadBy 注解的类/接口，则标记方式为继承的类/实现的接口。
         * 如果一个类通过继承一个类/实现一个接口，而该类/接口标记了一个注解，该注解标记了一个 LoadBy 注解，则标记方式为该注解。
         */
        val loadItemsMap =
            HashMap<Class<out Loader>, MutableMap<Class<*>, MutableMap<Class<*>, LoadItem>>>()

        val registers = classRegister.map { context.getBean(Class.forName(it)) as ClassRegister }

        val bindMap = HashMap<Class<*>, MutableMap<String, Class<*>>>()
        val beanFactoryMap = HashMap<Class<*>, Class<BeanFactory<*>>>()
//        val beanFactoryList = ArrayList<Class<out BeanFactory<*>>>()

        classes.forEach { clazz ->

            val binds = ArrayList<Class<*>>()
            checkAutoBind(clazz, binds)
            binds.forEach { if (clazz.isBean) bindMap.getOrPut(it, hashMapOf())[clazz.named] = clazz }

            if (BeanFactory::class.java.isAssignableFrom(clazz)) {
                // beanFactoryList.add(clazz as Class<out BeanFactory<*>>)
                clazz.genericInterfaces.forEach {
                    RelType.create(it).apply {
                        if (realClass == BeanFactory::class.java) {
                            generics?.let { type ->
                                beanFactoryMap[type[0].realClass] = clazz as Class<BeanFactory<*>>
                            }
                        }
                    }
                }
            }

            registers.forEach { it.register(clazz) }
            searchLoadBy(clazz, clazz, loadItemsMap)
        }

        bindMap.mapMap { (clazz, binds) ->
            NoInstanceClassContext(clazz).also { context.registerClass(it) } to binds
        }.forEach { (ctx, binds) ->
            ctx.putBinds(binds.mapMap { (named, bindClass) -> named to context.findContext(bindClass) })
        }

        beanFactoryMap.forEach { (type, factoryType) ->
            BeanFactoryClassContext(context, context.findContext(factoryType) as ClassContext<BeanFactory<Any>>, type as Class<Any>)
                .apply { context.registerClass(this) }
        }

        val loaders = ArrayList<Loader>()
        loadItemsMap.keys.forEach {
            loaders.add(
                context.getBean(it) ?: error("在应用加载的过程中试图获取 Loader(${it.name}) 的实例失败！")
            )
        }

        loaders.sortBy { it.priority() }

        loaders.forEach { loader ->
            loadItemsMap[loader::class.java]?.values?.forEach { loader.load(it.values) }
        }
    }

    open fun checkAutoBind(clazz: Class<*>, binds: ArrayList<Class<*>>) {
        if (clazz.isInterface) if (clazz.getAnnotation(AutoBind::class.java) != null) binds.add(clazz)
        checkAutoBind(clazz.superclass ?: return, binds)
        for (iC in clazz.interfaces) {
            checkAutoBind(iC, binds)
        }
    }

    open fun searchLoadBy(
        loadClass: Class<*>,
        searchClass: Class<*>,
        loadItemsMap: HashMap<Class<out Loader>, MutableMap<Class<*>, MutableMap<Class<*>, LoadItem>>>
    ) {
        searchClass.getAnnotation(LoadBy::class.java)?.let {
            if (it.mastBean) if (!loadClass.isBean()) return@let
            addLoadItem(loadClass, searchClass, null, it, loadItemsMap)
        }

        val annotationInstances = searchClass.annotations
        for (annotationInstance in annotationInstances) {
            val annotationClass = annotationInstance::class.java.interfaces[0]
            annotationClass.getAnnotation(LoadBy::class.java)?.let {
                if (it.mastBean) if (!loadClass.isBean()) return@let
                addLoadItem(loadClass, annotationClass, annotationInstance, it, loadItemsMap)
            }
        }

        val superClass = searchClass.superclass
        if (superClass != null) searchLoadBy(loadClass, superClass, loadItemsMap)

        val interfaces = searchClass.interfaces
        for (i in interfaces) {
            searchLoadBy(loadClass, i, loadItemsMap)
        }
    }

    open fun addLoadItem(
        loadClass: Class<*>,
        targetClass: Class<*>,
        annotationInstance: Annotation?,
        loadBy: LoadBy,
        loadItemsMap: HashMap<Class<out Loader>, MutableMap<Class<*>, MutableMap<Class<*>, LoadItem>>>
    ) {
        loadItemsMap.getOrPut(Class.forName(loadBy.value.java.name, false, classLoader) as Class<out Loader>, HashMap())
            .getOrPut(targetClass, HashMap())[loadClass] = LoadItem(loadClass, targetClass, annotationInstance)
    }

}
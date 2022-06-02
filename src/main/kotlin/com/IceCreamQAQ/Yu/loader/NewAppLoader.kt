package com.IceCreamQAQ.Yu.loader

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.BindableClassContext.Companion.putBinds
import com.IceCreamQAQ.Yu.di.ContextImpl
import com.IceCreamQAQ.Yu.di.NoInstanceClassContext
import com.IceCreamQAQ.Yu.di.isBean
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.mapMap
import com.IceCreamQAQ.Yu.mapOf
import com.IceCreamQAQ.Yu.named
import com.IceCreamQAQ.Yu.util.findClassByPackage
import com.IceCreamQAQ.Yu.util.getOrPut
import com.IceCreamQAQ.Yu.util.type.RelType

open class NewAppLoader(
    open val classLoader: ClassLoader,
    open val context: ContextImpl,
    open val scanPackages: List<String>,
    open val classRegister: List<String>,
) {

    open fun load() {
        val classes = HashSet<Class<*>>()
        scanPackages.forEach { classes.addAll(classLoader.findClassByPackage(it)) }

        /***
         * 数据结构
         * Map<Loader, Map<Class(标记方式), Map<Class(被扫描类), LoadItem>>>
         * 一个类上出现多种不同的标记方式指向同一个 Loader，则可以为每个标记方式，分别加载一次该类。
         * 如果一个类通过标记一个标记了 LoadBy 注解的注解，则标记方式为注解。
         * 如果一个类通过继承一个类/实现一个接口，指向一个标记 LoadBy 注解的类/接口，则标记方式为继承的类/实现的接口。
         * 如果一个类通过继承一个类/实现一个接口，而该类/接口标记了一个注解，该注解标记了一个 LoadBy 注解，则标记方式为改注解。
         */
        val loadItemsMap =
            HashMap<Class<out Loader>, MutableMap<Class<*>, MutableMap<Class<*>, NewLoadItem>>>()

        val registers = classRegister.map { context.getBean(Class.forName(it)) as ClassRegister }

        val bindMap = HashMap<Class<*>, MutableMap<String, Class<*>>>()
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
                            generics?.let {  }
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
        loadItemsMap: HashMap<Class<out Loader>, MutableMap<Class<*>, MutableMap<Class<*>, NewLoadItem>>>
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
        loadItemsMap: HashMap<Class<out Loader>, MutableMap<Class<*>, MutableMap<Class<*>, NewLoadItem>>>
    ) {
        loadItemsMap.getOrPut(loadBy.value.java, HashMap())
            .getOrPut(targetClass, HashMap())[loadClass] = NewLoadItem(loadClass, targetClass, annotationInstance)
    }

}
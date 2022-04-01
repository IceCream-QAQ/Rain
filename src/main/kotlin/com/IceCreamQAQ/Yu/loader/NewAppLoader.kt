package com.IceCreamQAQ.Yu.loader

import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.di.BeanFactory
import com.IceCreamQAQ.Yu.di.ContextImpl
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.isAbstract
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.util.findClassByPackage
import com.IceCreamQAQ.Yu.util.getOrPut
import java.lang.reflect.Modifier

open class NewAppLoader(
    val classLoader: ClassLoader,
    val context: ContextImpl,
    val scanPackages: List<String>,
    val classRegister: List<String>,
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



        classes.forEach { clazz ->
            if (BeanFactory::class.java.isAssignableFrom(clazz)) {

            } else {
//                if (clazz.isInterface || clazz.isAbstract)
            }

            registers.forEach { it.register(clazz) }
            searchLoadBy(clazz, clazz, loadItemsMap)
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
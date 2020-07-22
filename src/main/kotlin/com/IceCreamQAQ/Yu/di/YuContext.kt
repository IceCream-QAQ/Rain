package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.ClassRegister
import com.alibaba.fastjson.JSONObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class YuContext(private val configer: ConfigManager, private val logger: AppLogger) : ClassRegister {

    private val classContextMap = HashMap<String, ClassContext>()

    override fun register(clazz: Class<*>) {
        register(clazz, false)
    }

    fun register(context: ClassContext) {
        classContextMap[context.name] = context
    }

    fun register(clazz: Class<*>, force: Boolean) {
        if (classContextMap.containsKey(clazz.name)) return
        if (!force) if (clazz.getAnnotation(NotSearch::class.java) != null) return

        val classContext = ClassContext(clazz.name, clazz, false)
        classContextMap[clazz.name] = classContext

        val instanceName = clazz.getAnnotation(Named::class.java)?.value ?: ""

        if (!clazz.isInterface) {
            val binds = ArrayList<Class<*>>()
            checkAutoBind(clazz, binds)

            for (bind in binds) {
                val bcc = getClassContextOrRegister(bind, force)
                bcc.putBind(instanceName, classContext)
            }
        }
    }

    fun getClassContextOrRegister(clazz: Class<*>, force: Boolean): ClassContext {
        return classContextMap[clazz.name] ?: {
            register(clazz, force)
            classContextMap[clazz.name] ?: error("Cant Init Class: ${clazz.name}.")
        }()
    }

    fun checkAutoBind(clazz: Class<*>, binds: ArrayList<Class<*>>) {
        if (clazz.isInterface) if (clazz.getAnnotation(AutoBind::class.java) != null) binds.add(clazz)
        checkAutoBind(clazz.superclass ?: return, binds)
        for (iC in clazz.interfaces) {
            checkAutoBind(iC, binds)
        }
    }

    fun checkClassMulti(): Boolean = true

    //    private val context: MutableMap<String, MutableMap<String, Any>> = ConcurrentHashMap()
    private var factoryManager: BeanFactoryManager? = null

    init {
        putBean(this)
        putBean(configer)
        putBean(logger)
        factoryManager = newBean(BeanFactoryManager::class.java, save = true)
                ?: throw RuntimeException("Yu Init Err! Cant new BeanFactoryManager!")
    }

    operator fun <T> get(clazz: Class<T>): T? {
        return getBean(clazz)
    }

    fun <T> getBean(clazz: Class<T>, name: String? = null): T? {
        return getBean(clazz.name, name) as? T
    }

    fun getBean(clazz: String, name: String? = null): Any? {
        val context = classContextMap[clazz] ?: return newBean(Class.forName(clazz), name, save = true)
        val bean = if (name == null) context.defaultInstance else null
        if (bean != null) return bean

        if (clazz == "com.icecreamqaq.test.yu.TestInterface") {
            println("")
        }
        if (clazz == "com.icecreamqaq.test.yu.TestInterfaceImpl") {
            println("")
        }

        return context.getInstance(name) ?: {
            val bc = context.binds?.get(name ?: "")
            if (bc == null) null
            else getBean(bc.name, name)
        }() ?: newBean(Class.forName(clazz), name, true)
    }

    fun putBean(obj: Any, name: String = "") {
        putBean(obj::class.java, name, obj)
    }

    fun putBean(clazz: Class<*>, name: String, obj: Any) {
        val context = getClassContextOrRegister(clazz, false)
        context.putInstance(name, obj)
    }

    private fun checkAutoBind(clazz: Class<*>?, name: String, obj: Any) {
        val autoBind = clazz?.getAnnotation(AutoBind::class.java)
        if (autoBind != null) putBean(clazz, name, obj)
    }

    fun injectBean(obj: Any) {
        var clazz: Class<*>? = obj.javaClass

        val fields = ArrayList<Field>()
        while (clazz != null) {
            fields.addAll(Arrays.asList(*clazz.declaredFields))
            clazz = clazz.superclass
        }

        for (field in fields) {
//            val inject = field.getAnnotation(com.IceCreamQAQ.Yu.annotation.Inject::class.java)
//            if (inject != null) {
//                var injectType: String = inject.value.java.name
//                if (injectType.toLowerCase() == "com.icecreamqaq.yu.annotation.inject") injectType = field.type.name
//                field.isAccessible = true
//                field[obj] = getBean(injectType, inject.name)
//                continue
//            }

            val config = field.getAnnotation(Config::class.java)
            if (config != null) {
                val key = config.value
                val type = field.type

                val value = when {
                    isList(type) -> configer.getArray(key, (field.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>)
                    isArray(type) -> configer.getArray(key, type.componentType)?.toTypedArray()
                    else -> configer.get(key, field.type)
                } ?: field.getAnnotation(Default::class.java)?.value

                if (value != null) {
                    field.isAccessible = true
                    field[obj] = value
                }
                continue
            }

            val injectJsr = field.getAnnotation(Inject::class.java)
            if (injectJsr != null) {
                val name = field.getAnnotation(Named::class.java)?.value

                field.isAccessible = true
                val b = getBean(field.type.name,
                        if (name != null && name.startsWith("{")) name.substring(1).substring(0, name.length - 2)
                        else name
                )
                field[obj] = b
            }
        }
    }

    fun <T> newBean(clazz: Class<T>, name: String? = null, save: Boolean = false, force: Boolean = false): T? {
        val context = classContextMap[clazz.name] ?: getClassContextOrRegister(clazz, force)

        val bean = (context.factory as? BeanFactory<T>)?.createBean(clazz, name ?: "") ?: createBeanInstance(clazz)
//        ?: {
//            val bc = context.binds?.get(name)
//            if (bc == null) null
//            else newBean(bc.clazz, name, save, force)
//        }() as T?
        ?: return null


        if (save) putBean(bean, name ?: clazz.getAnnotation(Named::class.java)?.value ?: "")
        injectBean(bean)
        return bean
    }

//    fun getBindBean(context: ClassContext,name: String):Any?{
//
//    }

    private fun <T> createBeanInstance(clazz: Class<T>): T? {
        if (!clazz.isBean()) return null;
        val constructorNum = clazz.constructors.size
        if (constructorNum < 1) return null
        val constructors = clazz.constructors

        var inject: Constructor<*>? = null

        for (constructor in constructors) {
            if (constructor.parameters.isEmpty()) {
                continue
            }

            if (constructor.getAnnotation(Inject::class.java) == null) continue
            inject = constructor
            break
        }

        if (inject == null)
            return try {
                clazz.getConstructor().newInstance()
            } catch (e: Exception) {
                null
            }


        val paras = inject.parameters
        val objs = arrayOfNulls<Any>(paras.size)

        for (i in paras.indices) {
            val para = paras[i]
            val name = para.getAnnotation(Named::class.java)?.value ?: ""
            objs[i] = getBean(para.type, name)
        }

        return inject.newInstance(*objs) as? T
    }

    private fun isList(clazz: Class<*>): Boolean {
        if (clazz.name == List::class.java.name) return true
        val i = clazz.interfaces
        for (inf in i) {
            return isList(inf)
        }
        val s = clazz.superclass ?: return false
        return isList(s)
    }

    private fun isArray(clazz: Class<*>): Boolean {
        return clazz.componentType != null
    }


}
package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.ClassRegister
import com.IceCreamQAQ.Yu.toUpperCaseFirstOne
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType
import javax.inject.Inject
import javax.inject.Named

open class ContextOldImpl(val configer: ConfigManager) : ClassRegister {

    private val classContextMap = HashMap<String, ClassContext>()

    override fun register(clazz: Class<*>) {
        register(clazz, false)
    }

    open fun register(context: ClassContext) {
        classContextMap[context.name] = context
    }

    open fun register(clazz: Class<*>, force: Boolean) {
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

    open fun getClassContextOrRegister(clazz: Class<*>, force: Boolean): ClassContext {
        return classContextMap[clazz.name] ?: {
            register(clazz, force)
            classContextMap[clazz.name] ?: error("Cant Init Class: ${clazz.name}.")
        }()
    }

    open fun checkAutoBind(clazz: Class<*>, binds: ArrayList<Class<*>>) {
        if (clazz.isInterface) if (clazz.getAnnotation(AutoBind::class.java) != null) binds.add(clazz)
        checkAutoBind(clazz.superclass ?: return, binds)
        for (iC in clazz.interfaces) {
            checkAutoBind(iC, binds)
        }
    }

    open fun checkClassMulti(): Boolean = true

    //    private val context: MutableMap<String, MutableMap<String, Any>> = ConcurrentHashMap()
    protected var factoryManager: BeanFactoryManager? = null

    init {
//        if (configer.get("yu.context.mode", String::class.java) == "single") context = this
        putBean(this)
        putBean(configer)
        factoryManager = newBean(BeanFactoryManager::class.java, save = true)
            ?: throw RuntimeException("Yu Init Err! Cant new BeanFactoryManager!")
    }

    open operator fun <T> get(clazz: Class<T>): T? {
        return getBean(clazz)
    }

    open fun <T> getBean(clazz: Class<T>, name: String? = null): T? {
        return getBean(clazz.name, name) as? T
    }

    open fun getBean(clazz: String, name: String? = null): Any? {
        val context = classContextMap[clazz] ?: return newBean(Class.forName(clazz), name, save = true)
        val bean = if (name == null) context.defaultInstance else null
        if (bean != null) return bean

        return context.getInstance(name) ?: {
            val bc = context.binds?.get(name ?: "")
            if (bc == null) null
            else getBean(bc.name, name)
        }() ?: newBean(Class.forName(clazz), name, true)
    }

    protected open fun getBeanByType(clazz: String, name: String? = null, type: String = "bean"): Any? {
        return when (type) {
            "list" -> {
                val context = classContextMap[clazz] ?: return null
//                context.
                if (context.binds == null)
                    return if (context.defaultInstance != null) listOf(context.defaultInstance)
                    else listOf()
                val list = ArrayList<Any>()
                for (b in context.binds!!.values) {
                    list.add(b.defaultInstance ?: getBean(b.name, "") ?: error("在试图构建 List 容器时遇到无法响应的 Bean：${b.name}。"))
                }
                list
            }
            "map" -> {
                val context = classContextMap[clazz] ?: return null
                if (context.binds == null)
                    return if (context.defaultInstance != null) mapOf("" to context.defaultInstance)
                    else mapOf()
                val map = HashMap<String, Any>()
                for ((n, b) in context.binds!!) {
                    map[n] = b.defaultInstance ?: getBean(b.name, "") ?: error("在试图构建 Map 容器时遇到无法响应的 Bean：${b.name}。")
                }
                map
            }
            "bean" -> getBean(clazz, name)
            else -> null
        }
    }

    open fun putBean(obj: Any, name: String = "") {
        putBean(obj::class.java, name, obj)
    }

    open fun putBean(clazz: Class<*>, name: String, obj: Any) {
        val context = getClassContextOrRegister(clazz, false)
        context.putInstance(name, obj)
    }

    protected open fun checkAutoBind(clazz: Class<*>?, name: String, obj: Any) {
        val autoBind = clazz?.getAnnotation(AutoBind::class.java)
        if (autoBind != null) putBean(clazz, name, obj)
    }

    open fun injectBean(obj: Any) {
        var clazz: Class<*>? = obj.javaClass

        val fields = ArrayList<Field>()
        while (clazz != null) {
            fields.addAll(clazz.declaredFields.asList())
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
                    isList(type) -> configer.getArray(
                        key,
                        (field.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
                    )
                    isArray(type) -> configer.getArray(key, type.componentType)?.toTypedArray()
                    else -> configer.get(key, field.type)
                } ?: field.getAnnotation(Default::class.java)?.value

                if (value != null) {
                    try {
                        obj::class.java.getMethod("set${field.name.toUpperCaseFirstOne()}", field.type)
                            .invoke(obj, value)
                    } catch (e: Exception) {
                        field.isAccessible = true
                        field[obj] = value
                    }
                }
                continue
            }

            val injectJsr = field.getAnnotation(Inject::class.java)
            if (injectJsr != null) {
                val name = field.getAnnotation(Named::class.java)?.value

                field.isAccessible = true
                val ct = field.type
                val st = when {
//                    ct.isArray -> "array" to when (val a = (field.genericType as GenericArrayType).genericComponentType) {
//                        is Class<*> -> a
//                        is ParameterizedType -> a.rawType as Class<*>
//                        else -> error("在尝试构建 List 时，遇到无法解析的类型，在 ${obj::class.java.name}.${field.name}，类型：$a。")
//                    }
                    List::class.java.isAssignableFrom(ct) ->
                        "list" to when (val a = (field.genericType as ParameterizedType).actualTypeArguments[0]) {
                            is Class<*> -> a
                            is ParameterizedType -> a.rawType as Class<*>
                            is WildcardType -> a.upperBounds[0] as Class<*>
                            else -> error("在尝试构建 List 时，遇到无法解析的类型，在 ${obj::class.java.name}.${field.name}，类型：$a。")
                        }
                    Map::class.java.isAssignableFrom(ct) ->
                        "map" to when (val a = (field.genericType as ParameterizedType).actualTypeArguments[1]) {
                            is Class<*> -> a
                            is ParameterizedType -> a.rawType as Class<*>
                            is WildcardType -> a.upperBounds[0] as Class<*>
                            else -> error("在尝试构建 Map 时，遇到无法解析的类型，在 ${obj::class.java.name}.${field.name}，类型：$a。")
                        }
                    else -> "bean" to ct
                }
                val b = kotlin.runCatching {
                    getBeanByType(
                        st.second.name,
                        if (name != null && name.startsWith("{")) name.substring(1).substring(0, name.length - 2)
                        else name,
                        st.first
                    )
                }.getOrElse {
                    com.IceCreamQAQ.Yu.util.error("在获取 Bean 实例发生错误！在：${obj::class.java.name}.${field.name}。", it)
                }
                field[obj] = b
            }
        }


        try {
            obj::class.java.getMethod("init")
        } catch (e: NoSuchMethodException) {
            null
        }?.let {
            it.getAnnotation(Inject::class.java) ?: return@let
            it.invoke(obj)
        }
    }

    open fun <T> newBean(clazz: Class<T>, name: String? = null, save: Boolean = false, force: Boolean = false): T? {
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

    protected open fun <T> createBeanInstance(clazz: Class<T>): T? {
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

    protected open fun isList(clazz: Class<*>): Boolean {
        if (clazz.name == List::class.java.name) return true
        val i = clazz.interfaces
        for (inf in i) {
            return isList(inf)
        }
        val s = clazz.superclass ?: return false
        return isList(s)
    }

    protected open fun isArray(clazz: Class<*>): Boolean {
        return clazz.componentType != null
    }


}
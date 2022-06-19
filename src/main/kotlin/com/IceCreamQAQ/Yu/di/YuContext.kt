@file:Suppress("UNCHECKED_CAST")

package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.NotSearch
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.ClassRegister
import com.IceCreamQAQ.Yu.toUpperCaseFirstOne
import com.IceCreamQAQ.Yu.util.ClassUtil.isTypeOf
import com.IceCreamQAQ.Yu.util.error
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 应用上下文，具有注册Class的能力。每种具体的Class由[ClassContext]类实际管理。[ClassContext]可以看成
 * 是一种具体Class类型的承载容器。
 */
open class YuContext(val manager: ConfigManager, logger: AppLogger) : ClassRegister {

    private var factoryManager: BeanFactoryManager? = null

    private val classContextMap = HashMap<String, ClassContext>()


    companion object {
        private const val DEFAULT_NAME = ""
    }

    init {
        if (manager.get("yu.context.mode", String::class.java) == "singleton") context = this
        putBean(this)
        putBean(manager)
        putBean(logger)
        factoryManager = newBean(BeanFactoryManager::class.java, save = true)
            ?: throw RuntimeException("Yu Init Err! Cant new BeanFactoryManager!")
    }


    /**
     * 往上下文中注册一种Class
     *
     * @param clazz 需要注册的Class
     */
    override fun register(clazz: Class<*>) {
        register(clazz, false)
    }

    /**
     * 往上下文中注册一个容器
     *
     * @param context 容器
     */
    open fun register(context: ClassContext) {
        classContextMap[context.name] = context
    }

    /**
     * 往上下文中注册一种Class
     *
     * @param clazz 需要注册的Class
     * @param force 当类标注有[NotSearch]时，是否强制注册
     */
    open fun register(clazz: Class<*>, force: Boolean) {
        if (classContextMap.containsKey(clazz.name)) return
        if (!force) if (clazz.getAnnotation(NotSearch::class.java) != null) return

        val classContext = ClassContext(clazz.name, clazz, false)
        classContextMap[clazz.name] = classContext

        val instanceName = clazz.getAnnotation(Named::class.java)?.value ?: DEFAULT_NAME

        if (!clazz.isInterface) {
            val binds = ArrayList<Class<*>>()
            checkAutoBind(clazz, binds)

            for (bind in binds) {
                val bcc = getClassContextOrRegister(bind, force)
                bcc.putBind(instanceName, classContext)
            }
        }
    }

    /**
     * 获取对应Class类型的[ClassContext]，如果不存在则注册
     *
     * @param clazz [ClassContext]对应的Class类型
     * @param force 当类标注有[NotSearch]时，是否强制注册
     * @return 返回对应Class的[ClassContext]
     */
    open fun getClassContextOrRegister(clazz: Class<*>, force: Boolean): ClassContext {
        return classContextMap[clazz.name] ?: run {
            register(clazz, force)
            classContextMap[clazz.name] ?: error("Cant Init Class: ${clazz.name}.")
        }
    }

    open fun checkAutoBind(clazz: Class<*>, binds: ArrayList<Class<*>>) {
        if (clazz.isInterface) if (clazz.getAnnotation(AutoBind::class.java) != null) binds.add(clazz)
        checkAutoBind(clazz.superclass ?: return, binds)
        for (iC in clazz.interfaces) {
            checkAutoBind(iC, binds)
        }
    }

    /**
     * 是否检查类为单例
     */
    open fun checkClassSingleton(): Boolean = true

    // private val context: MutableMap<String, MutableMap<String, Any>> = ConcurrentHashMap()

    /**
     * 从上下文种获取对应Class类型的实例
     *
     * @param clazz Class类型
     */
    open operator fun <T> get(clazz: Class<T>): T? {
        return getBean(clazz)
    }

    /**
     * 给定对象的类型和名称，返回指定的对象
     *
     * @param clazz Class类型
     * @param name 对象名
     * @return 对象
     */
    open fun <T> getBean(clazz: Class<T>, name: String? = null): T? {
        return getBean(clazz.name, name) as? T
    }

    /**
     * 给定类型名称和对象名称，返回指定的对象
     *
     * @param clazz Class类型名
     * @param name 对象名
     * @return 对象
     */
    open fun getBean(clazz: String, name: String? = null): Any? {
        val context = classContextMap[clazz] ?: return newBean(Class.forName(clazz), name, save = true)
        val bean = if (name == null) context.defaultInstance else null
        if (bean != null) return bean

        return context.getInstance(name) ?: run {
            val bc = context.binds?.get(name ?: DEFAULT_NAME)
            if (bc == null) null
            else getBean(bc.name, name)
        } ?: newBean(Class.forName(clazz), name, true)
    }

    /**
     * 根据对象的类型和名称，返回指定的对象
     *
     * @param clazz Class类型名
     * @param name 对象名
     * @param type 上下文中的对象类型(只有list、map和bean三种)
     * @return 对象
     */
    protected open fun getBeanByType(clazz: String, name: String? = null, type: String = "bean"): Any? {
        return when (type) {
            "list" -> {
                val context = classContextMap[clazz] ?: return null
                if (context.binds == null)
                    return if (context.defaultInstance != null) listOf(context.defaultInstance)
                    else listOf()
                val list = ArrayList<Any>()
                for (b in context.binds!!.values) {
                    list.add(
                        b.defaultInstance ?: getBean(b.name, DEFAULT_NAME)
                        ?: error("在试图构建 List 容器时遇到无法响应的 Bean：${b.name}。")
                    )
                }
                list
            }
            "map" -> {
                val context = classContextMap[clazz] ?: return null
                if (context.binds == null)
                    return if (context.defaultInstance != null) mapOf(DEFAULT_NAME to context.defaultInstance)
                    else mapOf()
                val map = HashMap<String, Any>()
                for ((n, b) in context.binds!!) {
                    map[n] = b.defaultInstance ?: getBean(b.name, DEFAULT_NAME)
                            ?: error("在试图构建 Map 容器时遇到无法响应的 Bean：${b.name}。")
                }
                map
            }
            "bean" -> getBean(clazz, name)
            else -> null
        }
    }

    /**
     * 往上下文中直接添加一个命名的对象
     * 当名称为[DEFAULT_NAME]时，该obj被标记为默认对象
     *
     * @param name 该对象的名称
     * @param obj 实际对象
     */
    open fun putBean(obj: Any, name: String = DEFAULT_NAME) {
        putBean(obj::class.java, name, obj)
    }

    /**
     * 往上下文中直接添加一个给定类型的命名的对象
     * 当名称为[DEFAULT_NAME]时，该obj被标记为默认对象
     *
     * @param clazz Class类型
     * @param name 该对象的名称
     * @param obj 实际对象
     */
    open fun putBean(clazz: Class<*>, name: String, obj: Any) {
        val context = getClassContextOrRegister(clazz, false)
        context.putInstance(name, obj)
    }

    protected open fun checkAutoBind(clazz: Class<*>?, name: String, obj: Any) {
        val autoBind = clazz?.getAnnotation(AutoBind::class.java)
        if (autoBind != null) putBean(clazz, name, obj)
    }

    /**
     * 填充对象属性
     *
     * @param bean 具体的bean
     */
    open fun populateBean(bean: Any) {
        var clazz: Class<*>? = bean.javaClass

        val fields = mutableListOf<Field>()
        while (clazz != null) {
            fields.addAll(clazz.declaredFields.asList())
            clazz = clazz.superclass
        }

        for (field in fields) {
            // val inject = field.getAnnotation(com.IceCreamQAQ.Yu.annotation.Inject::class.java)
            // if (inject != null) {
            //     var injectType: String = inject.value.java.name
            //     if (injectType.toLowerCase() == "com.icecreamqaq.yu.annotation.inject") injectType = field.type.name
            //     field.isAccessible = true
            //     field[obj] = getBean(injectType, inject.name)
            //     continue
            // }

            val config = field.getAnnotation(Config::class.java)
            if (config != null) {
                val key = config.value
                val type = field.type

                val value = when {
                    isTypeOf(List::class.java, type) -> manager.getArray(
                        key,
                        (field.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
                    )
                    isArray(type) -> manager.getArray(key, type.componentType)?.toTypedArray()
                    else -> manager.get(key, field.type)
                } ?: field.getAnnotation(Default::class.java)?.value

                if (value != null) {
                    try {
                        bean::class.java.getMethod("set${field.name.toUpperCaseFirstOne()}", field.type)
                            .invoke(bean, value)
                    } catch (e: Exception) {
                        field.isAccessible = true
                        field[bean] = value
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
                    // ct.isArray -> "array" to when (val a = (field.genericType as GenericArrayType).genericComponentType) {
                    //     is Class<*> -> a
                    //     is ParameterizedType -> a.rawType as Class<*>
                    //     else -> error("在尝试构建 List 时，遇到无法解析的类型，在 ${obj::class.java.name}.${field.name}，类型：$a。")
                    // }
                    isTypeOf(ct, List::class.java) ->
                        "list" to when (val a = (field.genericType as ParameterizedType).actualTypeArguments[0]) {
                            is Class<*> -> a
                            is ParameterizedType -> a.rawType as Class<*>
                            is WildcardType -> a.upperBounds[0] as Class<*>
                            else -> error("在尝试构建 List 时，遇到无法解析的类型，在 ${bean::class.java.name}.${field.name}，类型：$a。")
                        }
                    isTypeOf(ct, Map::class.java) ->
                        "map" to when (val a = (field.genericType as ParameterizedType).actualTypeArguments[1]) {
                            is Class<*> -> a
                            is ParameterizedType -> a.rawType as Class<*>
                            is WildcardType -> a.upperBounds[0] as Class<*>
                            else -> error("在尝试构建 Map 时，遇到无法解析的类型，在 ${bean::class.java.name}.${field.name}，类型：$a。")
                        }
                    else -> "bean" to ct
                }
                val target = kotlin.runCatching {
                    getBeanByType(
                        st.second.name,
                        if (name != null && name.startsWith("{")) {
                            name.substring(1).substring(0, name.length - 2)
                        } else {
                            name
                        },
                        st.first
                    )
                }.getOrElse {
                    error("在获取 Bean 实例发生错误！在：${bean::class.java.name}.${field.name}。", it)
                }
                field[bean] = target
            }
        }


        try {
            bean::class.java.getMethod("init")
        } catch (e: NoSuchMethodException) {
            null
        }?.let {
            it.getAnnotation(Inject::class.java) ?: return@let
            it.invoke(bean)
        }
    }

    /**
     * 用给定的类型和名称实例化一个对象
     *
     * @param clazz Class类型
     * @param name 实例名称
     * @param save 实例化对象后是否保存进上下文
     * @param force 当类标注有[NotSearch]时，是否强制注册
     */
    open fun <T> newBean(clazz: Class<T>, name: String? = null, save: Boolean = false, force: Boolean = false): T? {
        val context = classContextMap[clazz.name] ?: getClassContextOrRegister(clazz, force)

        val bean =
            (context.factory as? BeanFactory<T>)?.createBean(clazz, name ?: DEFAULT_NAME) ?: createBeanInstance(clazz)
            // ?: {
            //     val bc = context.binds?.get(name)
            //     if (bc == null) null
            //     else newBean(bc.clazz, name, save, force)
            // }() as T?
            ?: return null


        if (save) putBean(bean, name ?: clazz.getAnnotation(Named::class.java)?.value ?: DEFAULT_NAME)
        populateBean(bean)
        return bean
    }

    // fun getBindBean(context: ClassContext,name: String):Any?{
    //
    // }

    /**
     * 根据给定的Class类型，实例化一个对象
     * 此方法返回的对象是个中间态，还未完成属性填充，不应该直接使用
     *
     * @param clazz Class类型
     * @return 实例化出来的对象
     */
    protected open fun <T> createBeanInstance(clazz: Class<T>): T? {
        if (!clazz.isBean()) return null
        val constructorNum = clazz.constructors.size
        if (constructorNum < 1) return null
        val constructors = clazz.constructors
        var defaultConstructor: Constructor<*>? = null

        var injectedConstructor: Constructor<*>? = null

        for (constructor in constructors) {
            if (constructor.parameters.isEmpty()) {
                defaultConstructor = constructor
                continue
            }

            if (constructor.getAnnotation(Inject::class.java) == null) continue
            injectedConstructor = constructor
            break
        }

        if (injectedConstructor == null) {
            return defaultConstructor?.newInstance()?.let {
                clazz.cast(it)
            } ?: throw IllegalArgumentException("缺少默认构造器")
        }

        val params = injectedConstructor.parameters
        val objs = arrayOfNulls<Any>(params.size)

        for (i in params.indices) {
            val para = params[i]
            val name = para.getAnnotation(Named::class.java)?.value ?: DEFAULT_NAME
            objs[i] = getBean(para.type, name)
        }

        return injectedConstructor.newInstance(*objs) as? T
    }

    protected open fun isArray(clazz: Class<*>): Boolean {
        return clazz.componentType != null
    }

}

var context: YuContext? = null

class ValueObj<T>(var obj: T) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>) = obj

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        obj = value
    }
}

class MultiModeNotSupport<T> : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        error("当 YuContext 不处于 singleton 模式时，不允许 Kotlin inject 方式注入！")
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        error("当 YuContext 不处于 singleton 模式时，不允许 Kotlin inject 方式注入！")
    }

}

inline fun <reified T> inject(name: String = ""): ReadWriteProperty<Any, T> =
    if (context == null) MultiModeNotSupport()
    else ValueObj(context!!.getBean(T::class.java, name)!!)

inline fun <reified T> config(name: String): ReadWriteProperty<Any, T> =
    if (context == null) MultiModeNotSupport()
    else ValueObj(context!!.manager.get(name, T::class.java)!!)

inline fun <reified T> configArray(name: String): ReadWriteProperty<Any, List<T>> =
    if (context == null) MultiModeNotSupport()
    else ValueObj(context!!.manager.getArray(name, T::class.java)!!)
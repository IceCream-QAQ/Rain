package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Default
import com.IceCreamQAQ.Yu.annotation.Inject
import com.alibaba.fastjson.JSONObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Named

class YuContext(private val configer: ConfigManager, private val logger: AppLogger) {

    private val context: MutableMap<String, MutableMap<String, Any>> = ConcurrentHashMap()

    init {
        putBean(this)
        putBean(logger)
    }

    fun <T> getBean(clazz: Class<T>, name: String = ""): T? {
        return getBean(clazz.name, name) as? T
    }

    fun getBean(clazz: String, name: String = ""): Any? {
        return context[clazz]?.get(name)
    }

    fun putBean(obj: Any, name: String = "") {
        putBean(obj::class.java, name, obj)
    }

    fun putBean(clazz: Class<*>, name: String, obj: Any) {
        val cn = clazz.name
        var objs = context[cn] ?:{
            val objs = ConcurrentHashMap<String,Any>()
            context[cn] = objs
            objs
        }()
        objs[name] = obj

        checkAutoBind(clazz.superclass, name, obj)
        for (i in clazz.interfaces) {
            checkAutoBind(i, name, obj)
        }
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
            val inject = field.getAnnotation(Inject::class.java)
            if (inject != null) {
                var injectType: String = inject.value.java.name
                if (injectType.toLowerCase() == "com.icecreamqaq.yu.annotation.inject") injectType = field.type.name
                field.isAccessible = true
                field[obj] = getBean(injectType, inject.name)
                continue
            }

            val config = field.getAnnotation(Config::class.java)
            if (config != null) {
                val key = config.value
                val type = field.type

                val value = when{
                    isList(type) -> configer.getArray(key,(field.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>)
                    isArray(type) -> configer.getArray(key,type.componentType)?.toTypedArray()
                    else -> configer.get(key, field.type)
                }

                if (value == null) {
                    val default = field.getAnnotation(Default::class.java)
                    if (default != null) {
                        field.isAccessible = true
                        field[obj] = default.value
                    }
                } else {
                    field.isAccessible = true
                    field[obj] = value
                }
                continue
            }

            val injectJsr = field.getAnnotation(javax.inject.Inject::class.java)
            if (injectJsr != null) {
                val name = field.getAnnotation(Named::class.java)?.value ?:""

                field.isAccessible = true
                field[obj] = getBean(field.type.name, name)
            }
        }
    }

    fun <T> newBean(clazz: Class<T>, name: String? = null, save: Boolean = false): T? {
        val bean = createBeanInstance(clazz)
        if (save) putBean(bean!!, name ?: clazz.getAnnotation(Named::class.java)?.value ?: "")
        injectBean(bean!!)
        return bean
    }

    private fun <T> createBeanInstance(clazz: Class<T>): T? {
        val constructorNum = clazz.constructors.size
        if (constructorNum < 1) return null
        val constructor: Constructor<*> = clazz.constructors[0]

        val paras = constructor.parameters

        if (paras.size == 0) {
            return clazz.newInstance()
        }

        val objs = arrayOfNulls<Any>(paras.size)
        for (i in paras.indices) {
            val para = paras[i]
            val inject = para.getAnnotation(Inject::class.java) ?: return null
            objs[i] = getBean(para.type)
        }

        return constructor.newInstance(objs) as? T
    }

    private fun isList(clazz: Class<*>): Boolean {
        if (clazz.name == List::class.java.name)return true
        val i = clazz.interfaces
        for ( inf in i) {
            return isList(inf)
        }
        val s=clazz.superclass?:return false
        return isList(s)
    }

    private fun isArray(clazz: Class<*>): Boolean {
        return clazz.componentType != null
    }

}
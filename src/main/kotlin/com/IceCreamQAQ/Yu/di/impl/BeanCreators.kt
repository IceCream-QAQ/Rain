package com.IceCreamQAQ.Yu.di.impl

import com.IceCreamQAQ.Yu.*
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Nullable
import com.IceCreamQAQ.Yu.di.BeanCreator
import java.lang.reflect.Constructor
import javax.inject.Named
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.javaType
import kotlin.reflect.jvm.javaType

class NoPublicConstructorBeanCreator<T>(val clazz: Class<T>) : BeanCreator<T> {
    override fun invoke(): Nothing = error("类 ${clazz.name} 没有任何公开的构造函数！")
}

open class DefaultConstructorBeanCreator<T>(val constructor: Constructor<T>) : BeanCreator<T> {
    override fun invoke(): T =
        constructor.newInstance()
}

open class InjectConstructorBeanCreator<T>(
    val context: ContextImpl,
    val constructor: Constructor<T>
) : BeanCreator<T> {

    open val readers = constructor.parameters.mapIndexed { i, it ->

        val name = it.annotation<Named>()?.value
        val nullable = it.hasAnnotation<Nullable>()

        val reader = it.annotation<Config>()?.let { config ->
            context.getConfigReader(config.value, it.parameterizedType)
        } ?: context.getDataReader(it.parameterizedType)

        fun injectNullError(): Nothing =
            error("在创建类: ${constructor.nameWithParamsFullClass} 实例时，第 $i 参数注入值为空！")

        if (nullable)
            if (name == null) {
                { reader() }
            } else {
                { reader(name) }
            }
        else
            if (name == null) {
                { reader() ?: injectNullError() }
            } else {
                { reader(name) ?: injectNullError() }
            }
    }

    override fun invoke(): T =
        constructor.newInstance(*readers.arrayMap { it() })

}

open class KInjectConstructorBeanCreator<T>(
    val context: ContextImpl,
    val constructor: KFunction<T>,
    val javaConstructor: Constructor<T>
) : BeanCreator<T> {

    data class PP(val parameter: KParameter, val reader: () -> Any?)

    open val readers = constructor.parameters.mapIndexed { i, it ->

        val name = it.findAnnotation<Named>()?.value
        val nullable = it.type.isMarkedNullable
        val optional = it.isOptional

        val reader = it.findAnnotation<Config>()?.let { config ->
            context.getConfigReader(config.value, it.type.javaType)
        } ?: context.getDataReader(it.type.javaType)

        fun injectNullError(): Nothing =
            error("在创建类: ${javaConstructor.nameWithParamsFullClass} 实例时，第 $i 参数注入值为空！")

        PP(
            it,
            if (optional)
                if (name == null) {
                    { reader() }
                } else {
                    { reader(name) }
                }
            else
                if (nullable)
                    if (name == null) {
                        { reader() }
                    } else {
                        { reader(name) }
                    }
                else
                    if (name == null) {
                        { reader() ?: injectNullError() }
                    } else {
                        { reader(name) ?: injectNullError() }
                    }
        )
    }

    override fun invoke(): T =
        constructor.callBy(
            HashMap<KParameter, Any>().apply {
                readers.forEach { (param, reader) -> reader()?.let { put(param, it) } }
            }
        )

}
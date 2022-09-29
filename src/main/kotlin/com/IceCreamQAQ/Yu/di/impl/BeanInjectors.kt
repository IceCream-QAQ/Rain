package com.IceCreamQAQ.Yu.di.impl

import com.IceCreamQAQ.Yu.*
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.annotation.Nullable
import com.IceCreamQAQ.Yu.di.BeanInjector
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.*

class ReflectBeanInject<T>(
    val context: ContextImpl,
    val clazz: Class<T>
) : BeanInjector<T> {

    private val fields =
        clazz.allField
            .filter { !it.isStatic && (it.hasAnnotation<Inject>() || it.hasAnnotation<Config>()) }
            .mutableMap {
                val named = it.annotation<Named>()
                val nullable = it.annotation<Nullable>()
                val setMethod =
                    kotlin.runCatching {
                        clazz.getMethod("set${it.name.toUpperCaseFirstOne()}", it.type)
                    }.getOrNull()

                val dataReader =
                    it.annotation<Config>()?.let { config -> context.getConfigReader(config.value, it.genericType) }
                        ?: context.getDataReader(it.genericType)

                fun fieldInjectNullError(): Nothing =
                    error("在对类: ${clazz.name} 中的 Field: ${it.name} 进行注入时，注入值为空！")

                fun fieldSetterInjectNullError(setter: Method): Nothing =
                    error("在对类: ${clazz.name} 中的 Field: ${it.name} 进行 Setter: ${setter.nameWithParams} 注入时，注入值为空！")

                if (setMethod != null) {
                    val named = named ?: setMethod.annotation() ?: setMethod.parameters.first().annotation()
                    val nullable = nullable ?: setMethod.annotation() ?: setMethod.parameters.first().annotation()

                    val name = named?.value
                    if (name == null)
                        if (nullable == null)
                            { instance: T ->
                                setMethod.invoke(instance, dataReader())
                            }
                        else
                            { instance: T ->
                                setMethod.invoke(instance, dataReader() ?: fieldSetterInjectNullError(setMethod))
                            }
                    else
                        if (nullable == null)
                            { instance: T ->
                                setMethod.invoke(instance, dataReader(name))
                            }
                        else
                            { instance: T ->
                                setMethod.invoke(
                                    instance,
                                    dataReader(name) ?: fieldSetterInjectNullError(setMethod)
                                )
                            }
                } else {
                    val name = named?.value
                    if (name == null)
                        if (nullable == null)
                            { instance: T ->
                                if (!it.isAccessible) it.isAccessible = true
                                it.set(instance, dataReader())
                            }
                        else
                            { instance: T ->
                                if (!it.isAccessible) it.isAccessible = true
                                it.set(instance, dataReader() ?: fieldInjectNullError())
                            }
                    else
                        if (nullable == null)
                            { instance: T ->
                                if (!it.isAccessible) it.isAccessible = true
                                it.set(instance, dataReader(name))
                            }
                        else
                            { instance: T ->
                                if (!it.isAccessible) it.isAccessible = true
                                it.set(instance, dataReader(name) ?: fieldInjectNullError())
                            }
                }
            }.apply {
                addAll(
                    clazz.allMethod
                        .filter { it.isPublic && it.name.startsWith("set") && (it.hasAnnotation<Inject>() || it.hasAnnotation<Config>()) && it.parameters.size == 1 && it.isExecutable }
                        .map {
                            val named = it.annotation<Named>() ?: it.parameters.first().annotation()
                            val nullable = it.annotation<Nullable>() ?: it.parameters.first().annotation()

                            val dataReader =
                                it.annotation<Config>()?.let { config ->
                                    context.getConfigReader(
                                        config.value,
                                        it.parameters.first().parameterizedType
                                    )
                                } ?: context.getDataReader(it.parameters.first().parameterizedType)

                            fun injectNullError(): Nothing =
                                error("在对类: ${clazz.name} 中的 Setter: ${it.nameWithParams} 注入时，注入值为空！")

                            if (named == null)
                                if (nullable == null)
                                    { instance: T ->
                                        it.invoke(instance, dataReader())
                                    }
                                else
                                    { instance: T ->
                                        it.invoke(instance, dataReader() ?: injectNullError())
                                    }
                            else
                                if (nullable == null)
                                    { instance: T ->
                                        it.invoke(instance, dataReader(named.value))
                                    }
                                else
                                    { instance: T ->
                                        it.invoke(instance, dataReader(named.value) ?: injectNullError())
                                    }
                        }
                )
            }

    override fun invoke(bean: T): T {
        fields.forEach { it(bean) }
        return bean
    }

}

class KReflectBeanInject<T : Any>(
    val context: ContextImpl,
    val clazz: Class<T>
) : BeanInjector<T> {

    val fields = clazz.allField.filter { it.hasAnnotation<Config>() || it.hasAnnotation<Inject>() }
        .filter { it.kotlinProperty is KMutableProperty1<*, *> }
        .mutableMap {
            val kParam = it.kotlinProperty as KMutableProperty1<Any, Any?>
            val nullable = kParam.returnType.isMarkedNullable
            val accessible = kParam.visibility == KVisibility.PUBLIC

            val type = kParam.returnType.javaType
            val reader = it.annotation<Config>()?.let { context.getConfigReader(it.value, type) }
                ?: context.getDataReader(type)

            val name = it.annotation<Named>()?.value

            fun injectNullError(): Nothing =
                error("在对 Kotlin 类: ${clazz.name} 中的 Property: ${it.name} 进行注入时，注入值为空！")

            if (nullable)
                if (accessible)
                    if (name == null)
                        { instance: T -> reader()?.let { r -> it.set(instance, it) } }
                    else
                        { instance: T -> reader(name)?.let { r -> it.set(instance, it) } }
                else
                    if (name == null)
                        { instance: T ->
                            reader()?.let { r ->
                                it.isAccessible = true
                                it.set(instance, it)
                            }
                        }
                    else
                        { instance: T ->
                            reader(name)?.let { r ->
                                it.isAccessible = true
                                it.set(instance, it)
                            }
                        }
            else
                if (accessible)
                    if (name == null)
                        { instance: T -> it.set(instance, reader() ?: injectNullError()) }
                    else
                        { instance: T -> it.set(instance, reader(name) ?: injectNullError()) }
                else
                    if (name == null)
                        { instance: T ->
                            it.isAccessible = true
                            it.set(instance, reader() ?: injectNullError())
                        }
                    else
                        { instance: T ->
                            it.isAccessible = true
                            it.set(instance, reader(name) ?: injectNullError())
                        }
        }.apply {
            addAll(
                clazz.kotlin.memberFunctions
                    .filter {
                        it.visibility == KVisibility.PUBLIC && it.name.startsWith("set")
                                && (it.hasAnnotation<Inject>() || it.hasAnnotation<Config>())
                                && it.parameters.size == 2
                    }.map {
                        val instanceParameter = it.parameters.first()
                        val dataParameter = it.parameters.last()

                        val type = dataParameter.type.javaType
                        val nullable = dataParameter.type.isMarkedNullable
                        val optional = dataParameter.isOptional

                        val name = it.findAnnotation<Named>()?.value ?: dataParameter.findAnnotation<Named>()?.value

                        val reader = it.findAnnotation<Config>()?.let { context.getConfigReader(it.value, type) }
                            ?: context.getDataReader(type)

                        fun injectNullError(): Nothing =
                            error("在对 Kotlin 类: ${clazz.name} 中的 Setter: ${it.javaMethod?.nameWithParamsFullClass} 进行注入时，注入值为空！")

                        if (optional)
                            if (name == null)
                                { instance: T ->
                                    reader()?.let { r ->
                                        it.callBy(mapOf(instanceParameter to instance, dataParameter to r))
                                    }
                                    Unit
                                }
                            else
                                { instance: T ->
                                    reader(name)?.let { r ->
                                        it.callBy(mapOf(instanceParameter to instance, dataParameter to r))
                                    }
                                    Unit
                                }
                        else
                            if (nullable)
                                if (name == null)
                                    { instance: T ->
                                        reader()?.let { r ->
                                            it.call(instance, r)
                                        }
                                        Unit
                                    }
                                else
                                    { instance: T ->
                                        reader(name)?.let { r ->
                                            it.call(instance, r)
                                        }
                                        Unit
                                    }
                            else
                                if (name == null)
                                    { instance: T ->
                                        it.call(instance, reader() ?: injectNullError())
                                        Unit
                                    }
                                else
                                    { instance: T ->
                                        it.call(instance, reader(name) ?: injectNullError())
                                        Unit
                                    }
                    }
            )
        }

    override fun invoke(bean: T): T = bean.apply { fields.forEach { it(this) } }

}
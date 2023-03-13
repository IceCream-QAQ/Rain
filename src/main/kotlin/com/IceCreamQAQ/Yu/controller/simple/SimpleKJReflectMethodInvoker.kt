package com.IceCreamQAQ.Yu.controller.simple

import com.IceCreamQAQ.Yu.annotation.Nullable
import com.IceCreamQAQ.Yu.arrayMap
import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.controller.ControllerInstanceGetter
import com.IceCreamQAQ.Yu.controller.ProcessInvoker
import com.IceCreamQAQ.Yu.hasAnnotation
import com.IceCreamQAQ.Yu.nameWithParamsFullClass
import com.IceCreamQAQ.Yu.named
import com.IceCreamQAQ.Yu.util.type.RelType
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

/** 简单通用 Controller MethodInvoker 基础实现。
 * 类实现了 Controller Method 的 反射/Kotlin反射 调用。
 * 支持自动扫描参数，映射相关内容。
 */
abstract class SimpleKJReflectMethodInvoker<CTX : ActionContext, ATT>(
    val method: Method,
    val instance: ControllerInstanceGetter
) : ProcessInvoker<CTX> {

    companion object{
        fun Class<*>.checkSimple(): Class<*> {
            return when (this) {
                Char::class.javaPrimitiveType -> Char::class.javaObjectType
                Boolean::class.java -> Boolean::class.javaObjectType
                Byte::class.java -> Byte::class.javaObjectType
                Short::class.java -> Short::class.javaObjectType
                Int::class.java -> Int::class.javaObjectType
                Float::class.java -> Float::class.javaObjectType
                Long::class.java -> Long::class.javaObjectType
                Double::class.java -> Double::class.javaObjectType
                else -> this
            }
        }
    }

    class MethodParameterInjectFailedException(
        val parameter: MethodParam<*>,
        cause: Throwable
    ) : RuntimeException(
        "在为调用方法: ${parameter.method.nameWithParamsFullClass} 准备参数 ${parameter.fullName} 值时遇到问题。",
        cause
    )

    val fullName: String = method.nameWithParamsFullClass

    // 方法参数映射对象
    class MethodParam<ATT>(
        val method: Method,
        // 参数名，Java 参数获取 Named 注解。Kotlin 参数优先尝试获取 Named 注解，不存在则获取参数名。
        val name: String,
        // 参数类型
        val relType: RelType<*>,
        // 参数是否可空，Java 参数根据是否标记 Nullable 注解确定，Kotlin 参数则直接获取是否声明可空。一般下游实现无需关心本参数。
        val nullable: Boolean,
        // 是否可选参数，如果 Kotlin 参数具有默认值，则本项为 true，否则为 false。一般下游实现无需关心本参数。
        val optional: Boolean,
        // 参数默认值，根据参数 Default 注解内容确定。
        val default: String?,
        // 当方法是 Java 方法，则本项为参数反射对象，否则为 null。
        val reflectParam: Parameter?,
        // 当方法是 Kotlin 方法，则本项为参数 Kotlin反射 对象，否则为 null。
        val kReflectParam: KParameter?
    ) {
        // 附件参数，一般可用于存储下游生成信息。
        val type: Class<*> = relType.realClass.checkSimple()

        val fullName = StringBuilder(name)
            .apply {
                append(": ")
                append(relType)
                if (nullable) append("?")
                if (optional) append(" = DefaultValue")
            }.toString()

        var attachment: ATT? = null
        var valueChecker: ((Any?) -> Any?) = {
            kotlin.runCatching {
                if (it == null)
                    if (!nullable) throw NullPointerException("参数 $name 不能为空。")
                    else null
                else if (!type.isInstance(it)) throw IllegalArgumentException("参数 $name 类型不匹配，需求类型: ${type.name}，实际类型: ${it::class.java.name}。")
                else it
            }.getOrElse {
                throw MethodParameterInjectFailedException(this, it)
            }
        }

        companion object {
            inline fun <reified T : Annotation> MethodParam<*>.annotaton(): T? = annotation(T::class.java)

            inline fun <reified T : Annotation> MethodParam<*>.annotation(body: T.() -> Unit): T? =
                annotation(T::class.java)?.apply(body)

            inline fun <reified T : Annotation> MethodParam<*>.hasAnnotation(): Boolean = annotaton<T>() != null

        }

        fun <T : Annotation> annotation(type: Class<T>): T? =
            reflectParam?.getAnnotation(type)
                ?: kReflectParam?.annotations?.firstOrNull { type.isInstance(it) } as T?


        fun <T : Annotation> hasAnnotation(type: Class<T>): Boolean = annotation(type) != null
    }

    lateinit var invoker: suspend (CTX) -> Any?

    val resultFlag = method.returnType.name != "void"

    init {
        method.kotlinFunction?.let { kFun ->
            var instanceParam: KParameter? = null
            kFun.parameters.mapNotNull {
                if (it.kind == KParameter.Kind.INSTANCE) {
                    instanceParam = it
                    null
                } else MethodParam<ATT>(
                    method,
                    it.name ?: "",
                    RelType.create(it.type.javaType),
                    it.type.isMarkedNullable,
                    it.isOptional,
                    null,
                    null,
                    it
                )
            }.also { initParam(method, it.toTypedArray()) }
                .let {
                    invoker = { context ->
                        val paramMap = HashMap<KParameter, Any?>(kFun.parameters.size)
                        paramMap[instanceParam!!] = instance()
                        it.forEach {
                            paramMap[it.kReflectParam!!] = it.valueChecker(getParam(it, context))
                        }
                        kFun.callSuspendBy(paramMap)
                    }
                }
        } ?: method.parameters.mapNotNull {
            MethodParam<ATT>(
                method,
                it.named,
                RelType.create(it.type),
                it.hasAnnotation<Nullable>(),
                false,
                null,
                it,
                null
            )
        }.also { initParam(method, it.toTypedArray()) }
            .let {
                invoker = { context ->
                    method.invoke(
                        instance(),
                        it.arrayMap { it.valueChecker(getParam(it, context)) }
                    )
                }
            }

    }

    abstract fun initParam(method: Method, params: Array<MethodParam<ATT>>)
    abstract fun getParam(param: MethodParam<ATT>, context: CTX): Any?

    override suspend fun invoke(context: CTX): Any? {
        kotlin.runCatching {
            if (resultFlag) return invoker(context)
            invoker(context)
            return null
        }.getOrElse {
            if (it is InvocationTargetException) throw it.targetException
            else throw it
        }
    }


    open fun <T> Class<T>.simpleClassValueOf(): (String) -> T {
        fun m(body: (String) -> Any) = body as (String) -> T
        return when (this) {
            Char::class.java -> m { it[0] }
            Boolean::class.java -> m { it.toBoolean() }
            Byte::class.java -> m { it.toByte() }
            Short::class.java -> m { it.toShort() }
            Int::class.java -> m { it.toInt() }
            Float::class.java -> m { it.toFloat() }
            Long::class.java -> m { it.toLong() }
            Double::class.java -> m { it.toDouble() }
            else -> error("遇到了不支持的类型: ${this.name}。")
        }
    }

    open fun String.stringAsSimple(type: Class<*>): Any? =
        when (type) {
            Boolean::class.java, Boolean::class.javaObjectType -> toBoolean()
            Byte::class.java, Byte::class.javaObjectType -> toByte()
            Short::class.java, Short::class.javaObjectType -> toShort()
            Int::class.java, Int::class.javaObjectType -> toInt()
            Long::class.java, Long::class.javaObjectType -> toLong()
            Char::class.java, Char::class.javaObjectType -> get(0)
            Float::class.java, Float::class.javaObjectType -> toFloat()
            Double::class.java, Double::class.javaObjectType -> toDouble()
            else -> null
        }

}
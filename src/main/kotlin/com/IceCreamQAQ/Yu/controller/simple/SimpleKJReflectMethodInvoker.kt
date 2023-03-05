package com.IceCreamQAQ.Yu.controller.simple

import com.IceCreamQAQ.Yu.annotation.Nullable
import com.IceCreamQAQ.Yu.arrayMap
import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.controller.ControllerInstanceGetter
import com.IceCreamQAQ.Yu.controller.ProcessInvoker
import com.IceCreamQAQ.Yu.hasAnnotation
import com.IceCreamQAQ.Yu.named
import com.IceCreamQAQ.Yu.util.type.RelType
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

    // 方法参数映射对象
    class MethodParam<ATT>(
        // 参数名，Java 参数获取 Named 注解。Kotlin 参数优先尝试获取 Named 注解，不存在则获取参数名。
        val name: String,
        // 参数类型
        val type: RelType<*>,
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
        var attachment: ATT? = null
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
                    it.name ?: "",
                    RelType.create(it.type.javaType),
                    it.type.isMarkedNullable,
                    it.isOptional,
                    null,
                    null,
                    it
                )
            }.also { initParam(it.toTypedArray()) }
                .let {
                    invoker = { context ->
                        val paramMap = HashMap<KParameter, Any?>(kFun.parameters.size)
                        paramMap[instanceParam!!] = instance()
                        it.forEach {
                            paramMap[it.kReflectParam!!] =
                                getParam(it, context)
                                    .also { v -> if (v == null && !it.nullable && !it.optional) error("") }
                        }
                        kFun.callSuspendBy(paramMap)
                    }
                }
        } ?: method.parameters.mapNotNull {
            MethodParam<ATT>(
                it.named,
                RelType.create(it.type),
                it.hasAnnotation<Nullable>(),
                false,
                null,
                it,
                null
            )
        }.also { initParam(it.toTypedArray()) }
            .let {
                invoker = { context ->
                    method.invoke(
                        instance(),
                        it.arrayMap { getParam(it, context).apply { if (this == null && !it.nullable) error("") } }
                    )
                }
            }

    }

    abstract fun initParam(params: Array<MethodParam<ATT>>)
    abstract fun getParam(param: MethodParam<ATT>, context: CTX): Any?

    override suspend fun invoke(context: CTX): Any? {
        if (resultFlag) return invoker(context)
        invoker(context)
        return null
    }

}
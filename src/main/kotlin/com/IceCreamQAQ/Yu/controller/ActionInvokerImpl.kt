package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.toLowerCaseFirstOne
import java.lang.reflect.Method

open class ActionInvokerImpl(level: Int, method: Method, instance: Any) : RouterImpl(level), ActionInvoker {

    open lateinit var globalBefores: MutableList<MethodInvoker>
    open lateinit var globalAfters: MutableList<MethodInvoker>
    open lateinit var globalCatchs: MutableList<CatchInvoker>


    open lateinit var befores: Array<MethodInvoker>
    open val invoker: MethodInvoker = ReflectMethodInvoker(method, instance)
    open lateinit var afters: Array<MethodInvoker>
    open lateinit var catchs: Array<CatchInvoker>

    override fun invoke(path: String, context: ActionContext): Boolean {
        if (super.invoke(path, context)) return true
        try {
            for (before in globalBefores) {
                val o = before.invoke(context)
                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
            }
            for (before in befores) {
                val o = before.invoke(context)
                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
            }
            val result = invoker.invoke(context)
            context.onSuccess(result)
            for (after in globalAfters) {
                val o = after.invoke(context)
                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
            }
            for (after in afters) {
                val o = after.invoke(context)
                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
            }
        } catch (e: Exception) {
            val er = context.onError(e) ?: return true
            context["exception"] = er
            try {
                for (catch in globalCatchs) {
                    val o = catch.invoke(context, er)
                    if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
                }
                for (catch in catchs) {
                    val o = catch.invoke(context, er)
                    if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
                }
            } catch (ee: Exception) {
                throw context.onError(ee) ?: return true
            }
        }
        return true
    }

}
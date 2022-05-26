package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
import com.IceCreamQAQ.Yu.toLowerCaseFirstOne
import com.IceCreamQAQ.Yu.util.ControllerInvokeException
import com.IceCreamQAQ.Yu.util.bubbleSort
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

interface ActionInvoker {
    val method: Method
    val instance: Any

    val controllerName: String
    val actionName: String
    val actionFullName: String

    var befores: Array<MethodInvoker>
    var invoker: MethodInvoker
    var afters: Array<MethodInvoker>
    var catchs: Array<CatchInvoker>

    var interceptorInfo: InterceptorInfo?

    val width: Int

    fun createActionMethodInvoker(method: Method, instance: Any): MethodInvoker

    fun init(rootRouter: RouterInfo) {
        interceptorInfo?.let {
            fun <A : Annotation, I> listOf(type: KProperty1<InterceptorInfo, List<DoMethod<A, I>>>): List<DoMethod<A, I>> {
                val list = arrayListOf<DoMethod<A, I>>()
                list.addAll(type.get(it))
                list.addAll(type.get(rootRouter.interceptorInfo))
                return list
            }
            befores = makeInvokers(listOf(InterceptorInfo::befores), Before::except, Before::only, Before::weight)
            afters = makeInvokers(listOf(InterceptorInfo::afters), After::except, After::only, After::weight)
            catchs = makeInvokers(listOf(InterceptorInfo::catchs), Catch::except, Catch::only, Catch::weight)
        }
        invoker = createActionMethodInvoker(method, instance)
    }

    suspend operator fun invoke(context: ActionContext, paras: ParasMap): Boolean {
        return kotlin.runCatching {
            if (!perInvoke(context)) return false

            for (before in befores)
                runCatching(before.fullName, "Before") { before.invoke(context) }
                    ?.let { context[it::class.java.simpleName.toLowerCaseFirstOne()] = it }

            val r = postInvoke(context, runCatching(invoker.fullName, "Action") { invoker.invoke(context) })
            val f = postSuccess(context, context.onSuccess(r))
            if (!f) return true

            for (after in afters)
                runCatching(after.fullName, "Before") { after.invoke(context) }
                    ?.let { context[it::class.java.simpleName.toLowerCaseFirstOne()] = it }
            return true
        }.getOrElse {
            if (it is ControllerInvokeException) {
                val c = perError(context, it.cause)
                if (c != null) return c

                val e = postError(context, context.onError(it.cause)) ?: return true
                context["exception"] = e

                kotlin.runCatching {
                    for (catch in catchs)
                        runCatching(catch.fullName, "Catch") { catch.invoke(context, e) }
                            ?.let { o -> context[o::class.java.simpleName.toLowerCaseFirstOne()] = o }
                }.getOrElse { x ->
                    if (x is ControllerInvokeException) {
                        val cc = perError(context, it.cause)
                        if (cc != null) return cc

                        throw postError(context, context.onError(it.cause)) ?: return true
                    } else throw x
                }
                true
            } else throw it
        }
    }

    suspend fun perInvoke(context: ActionContext): Boolean = true
    suspend fun postInvoke(context: ActionContext, result: Any?): Any? = result
    suspend fun postSuccess(context: ActionContext, result: Any?): Boolean = true
    suspend fun perError(context: ActionContext, throwable: Throwable): Boolean? = null
    suspend fun postError(context: ActionContext, throwable: Throwable?): Throwable? = throwable

    private inline fun <T : Annotation, reified I> makeInvokers(
        list: List<DoMethod<T, I>>,
        exceptProp: KProperty1<T, Array<String>>,
        onlyProp: KProperty1<T, Array<String>>,
        wightProp: KProperty1<T, Int>,
    ): Array<I> {
        val name = actionName
        val mis = ArrayList<DoMethod<T, I>>()
        o@ for (m in list) {
            val (t, invoker) = m
            val except = exceptProp.get(t)
            if (!(except.size == 1 && except[0] == "")) for (s in except) if (s == name) continue@o

            val only = onlyProp.get(t)
            if (!(only.size == 1 && only[0] == "")) for (s in only) if (s != name) continue@o

            mis.add(m)
        }

        mis.bubbleSort { wightProp.get(this.annotation) }

        val a = arrayOfNulls<I>(mis.size)
        for (n in 0 until mis.size) a[n] = mis[n].invoker
        return a as Array<I>
    }

    private inline fun <R> runCatching(errorMethodFullName: String, errorType: String, block: () -> R) =
        runCatching(block).getOrElse {
            throw ControllerInvokeException(
                controllerName,
                actionName,
                actionFullName,
                errorMethodFullName,
                errorType,
                it
            )
        }


}

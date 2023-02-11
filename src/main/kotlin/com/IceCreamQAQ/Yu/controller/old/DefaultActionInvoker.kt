package com.IceCreamQAQ.Yu.controller.old

import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
import com.IceCreamQAQ.Yu.fullName
import com.IceCreamQAQ.Yu.toLowerCaseFirstOne
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

open class DefaultActionInvoker(level: Int, val method: Method, val instance: Any) : RouterImpl(level), ActionInvoker {

    //    open lateinit var globalBefores: MutableList<MethodInvoker>
//    open lateinit var globalAfters: MutableList<MethodInvoker>
//    open lateinit var globalCatchs: MutableList<CatchInvoker>
    val controllerName = method.fullName

    var interceptorInfo: InterceptorInfo? = null

    override lateinit var befores: Array<MethodInvoker>
    override lateinit var invoker: MethodInvoker
    override lateinit var afters: Array<MethodInvoker>
    override lateinit var catchs: Array<CatchInvoker>

    open fun createMethodInvoker(method: Method, instance: Any): MethodInvoker = ReflectMethodInvoker(method, instance)

    override fun init(rootRouter: RootRouter) {
        super.init(rootRouter)
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
        interceptorInfo = null

        invoker = createMethodInvoker(method, instance)
    }

    private inline fun <T : Annotation, reified I> makeInvokers(
        list: List<DoMethod<T, I>>,
        exceptProp: KProperty1<T, Array<String>>,
        onlyProp: KProperty1<T, Array<String>>,
        wightProp: KProperty1<T, Int>,
    ): Array<I> {
        val name = method.name
        val mis = ArrayList<DoMethod<T, I>>()
        o@ for (m in list) {
            val (t, invoker) = m
            val except = exceptProp.get(t)
            if (!(except.size == 1 && except[0] == "")) for (s in except) if (s == name) continue@o

            val only = onlyProp.get(t)
            if (!(only.size == 1 && only[0] == "")) for (s in only) if (s != name) continue@o

            mis.add(m)
        }
//        val a = mis.toTypedArray()

        for (i in mis.indices) {
            for (j in 0 until mis.size - 1 - i) {
                val c = mis[j]
                val n = mis[j + 1]
                val cw = wightProp.get(c.annotation)
                val nw = wightProp.get(n.annotation)
                if (cw > nw) {
                    mis[j] = n
                    mis[j + 1] = c
                }
            }
        }

        val a = arrayOfNulls<I>(mis.size)
        for (n in 0 until mis.size) a[n] = mis[n].invoker
        return a as Array<I>
    }

    override suspend fun invoke(path: String, context: ActionContext): Boolean {
        if (super.invoke(path, context)) return true
        try {
//            for (before in globalBefores) {
//                val o = before.invoke(context)
//                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
//            }
            for (before in befores) {
                val o = before.invoke(context)
                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
            }
            val result = invoker.invoke(context)
            context.onSuccess(result)
//            for (after in globalAfters) {
//                val o = after.invoke(context)
//                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
//            }
            for (after in afters) {
                val o = after.invoke(context)
                if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
            }
        } catch (e: Exception) {
            val er = context.onError(e) ?: return true
            context["exception"] = er
            try {
//                for (catch in globalCatchs) {
//                    val o = catch.invoke(context, er)
//                    if (o != null) context[o::class.java.simpleName.toLowerCaseFirstOne()] = o
//                }
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
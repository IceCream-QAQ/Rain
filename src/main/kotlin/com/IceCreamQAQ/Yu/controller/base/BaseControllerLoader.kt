package com.IceCreamQAQ.Yu.controller.base

import com.IceCreamQAQ.Yu.annotation.*
import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.isBean
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.net.URLClassLoader
import javax.inject.Inject

abstract class BaseControllerLoader : Loader {

    companion object {
        private val logger = LoggerFactory.getLogger(BaseControllerLoader::class.java)
    }

    protected abstract fun createMethodInvoker(instance: Any, method: Method): MethodInvoker
    protected abstract fun createCatchInvoker(
        instance: Any,
        method: Method,
        errorType: Class<out Throwable>
    ): CatchInvoker

    protected abstract fun getControllerRouter(controller: Class<*>): Router
    protected abstract fun checkAction(
        instance: Any,
        method: Method,
        controllerRouter: Router,
        interceptorInfo: InterceptorInfo,
        enableMethods: Array<String>
    ): ActionInfo?

    protected abstract fun loadSuccess()

    abstract val routerInfo: RouterInfo
    abstract val defaultMethods: Array<String>

    @Inject
    lateinit var context: YuContext

    override fun load(items: Map<String, LoadItem>) {
        for (item in items.values) {
            if (!item.type.isBean()) continue
            val clazz = item.type
            logger.debug("加载 Controller: ${clazz.name}。")
            val instance = context[clazz] ?: error("载入失败！无法获取 Controller: ${clazz.name} 的实例！")
            val enableMethods = clazz.getAnnotation(EnableMethod::class.java)?.value ?: defaultMethods

            makeController(clazz, instance, enableMethods)
            logger.debug("加载 Controller: ${clazz.name} 完成。")
        }
        loadSuccess()
    }

    open fun makeController(clazz: Class<*>, instance: Any, enableMethods: Array<String>) {
        val allMethods = arrayListOf<Method>().apply { getMethods(this, clazz) }

        val controllerRouter = getControllerRouter(clazz)

        val befores = ArrayList<DoMethod<Before, MethodInvoker>>()
        val afters = ArrayList<DoMethod<After, MethodInvoker>>()
        val catchs = ArrayList<DoMethod<Catch, CatchInvoker>>()


        val interceptorInfo = InterceptorInfo(befores, afters, catchs)

        for (method in allMethods) {
            val before = method.getAnnotation(Before::class.java)
            if (before != null) {
                logger.debug("加载 Controller: ${clazz.name} 中的 Before 拦截器: ${method.name}。")
                val beforeInvoker = createMethodInvoker(instance, method)
                val dm = DoMethod(before, beforeInvoker)
                if (method.getAnnotation(Global::class.java) != null) addGlobalBefore(dm)
                else befores.add(dm)
            }
            val after = method.getAnnotation(After::class.java)
            if (after != null) {
                logger.debug("加载 Controller: ${clazz.name} 中的 After 拦截器: ${method.name}。")
                val afterInvoker = createMethodInvoker(instance, method)
                val dm = DoMethod(after, afterInvoker)
                if (method.getAnnotation(Global::class.java) != null) addGlobalAfter(dm)
                else afters.add(dm)
            }
            val catch = method.getAnnotation(Catch::class.java)
            if (catch != null) {
                logger.debug("加载 Controller: ${clazz.name} 中的 Catch 拦截器: ${method.name}。")
                val catchInvoker = createCatchInvoker(instance, method, catch.error.java)
                val dm = DoMethod(catch, catchInvoker)
                if (method.getAnnotation(Global::class.java) != null) addGlobalCatch(dm)
                else catchs.add(dm)
            }
            checkAction(
                instance,
                method,
                controllerRouter,
                interceptorInfo,
                enableMethods
            )?.let { routerInfo.allAction.add(it) }
        }

    }

    open fun addGlobalBefore(doMethod: DoMethod<Before, MethodInvoker>) =
        routerInfo.interceptorInfo.befores.add(doMethod)

    open fun addGlobalAfter(doMethod: DoMethod<After, MethodInvoker>) = routerInfo.interceptorInfo.afters.add(doMethod)

    open fun addGlobalCatch(doMethod: DoMethod<Catch, CatchInvoker>) = routerInfo.interceptorInfo.catchs.add(doMethod)

    open fun getMethods(methods: MutableList<Method>, clazz: Class<*>) {
        methods.addAll(clazz.methods)
        getMethods(methods, clazz.superclass ?: return)
    }

}
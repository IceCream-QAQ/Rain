package com.IceCreamQAQ.Yu.controller.dss

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
import com.IceCreamQAQ.Yu.annotation.Path
import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.controller.dss.router.DssRouter
import com.IceCreamQAQ.Yu.controller.dss.router.NamedVariableMatcher
import com.IceCreamQAQ.Yu.controller.dss.router.RegexMatcher
import com.IceCreamQAQ.Yu.controller.dss.router.RouterMatcher
import com.IceCreamQAQ.Yu.di.YuContext
import java.lang.reflect.Method

/** 动静分离 ControllerLoader
 * 这是一个简单的 Controller 实现，用以技术性验证。
 * 这个简单的实现提供了基础的 Router 与 Action 实现，如果业务合适，可以直接基于本实现开展业务。
 *
 * 路由做了基础的静态匹配与动态匹配分离。
 */
abstract class DssControllerLoader<CTX : PathActionContext, ROT : DssRouter<CTX>, RootInfo : RootRouterProcessFlowInfo<CTX, ROT>, PI : ProcessInvoker<CTX>>(
    context: YuContext
) : ControllerLoader<CTX, ROT, RootInfo>(context) {


    override fun makeBefore(
        beforeAnnotation: Before,
        controllerClass: Class<*>,
        beforeMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        createMethodInvoker(controllerClass, beforeMethod, instanceGetter)
            ?.let { ProcessInfo(beforeAnnotation.weight, beforeAnnotation.except, beforeAnnotation.only, it) }


    override fun makeAfter(
        afterAnnotation: After,
        controllerClass: Class<*>,
        afterMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        createMethodInvoker(controllerClass, afterMethod, instanceGetter)
            ?.let { ProcessInfo(afterAnnotation.weight, afterAnnotation.except, afterAnnotation.only, it) }

    override fun makeCatch(
        catchAnnotation: Catch,
        controllerClass: Class<*>,
        catchMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        createCatchMethodInvoker(catchAnnotation.error.java, controllerClass, catchMethod, instanceGetter)
            ?.let { ProcessInfo(catchAnnotation.weight, catchAnnotation.except, catchAnnotation.only, it) }

    open fun <R> makePathMatcher(path: String, body: (path: String, matchers: List<Pair<String, String?>>) -> R): R {
        val realPathBuilder = StringBuilder()
        val psb = StringBuilder()

        var csb = realPathBuilder
        var matchFlag = false
        var regexFlag = false

        var locationFlag = false

        val matchList = ArrayList<Pair<String, String?>>()
        var i = 0

        var cName = ""


        while (i < path.length) {
            var c = path[i]

            fun end() {
                if (c == '\\') c = path[++i]
                csb.append(c)
            }

            if (!matchFlag) {
                if (c == '{') {
                    matchFlag = true
                    csb = psb
                } else {
                    locationFlag = true
                    end()
                }
            } else {
                when (c) {
                    ':' -> {
                        cName = psb.toString()
                        psb.clear()
                        regexFlag = true
                    }

                    '}' -> {
                        val (name, regex) = if (regexFlag) cName to psb.toString() else psb.toString() to ".*"
                        matchList.add(name to regex)
                        psb.clear()
                        matchFlag = false
                        regexFlag = false
                        csb = realPathBuilder
                        realPathBuilder.append("($regex)")
                    }

                    else -> end()
                }
            }
            i++
        }

        return body(if (locationFlag) realPathBuilder.toString() else "", matchList)
    }

    override fun controllerInfo(
        root: RootInfo,
        annotation: Annotation?,
        controllerClass: Class<*>,
        instanceGetter: ControllerInstanceGetter
    ): ControllerProcessFlowInfo<CTX, ROT>? {
        val channels = controllerChannel(annotation, controllerClass)
        var controllerRouter = root.router
        controllerClass.annotation<Path>()?.value?.split("/")?.forEach {
            controllerRouter = makePathMatcher(it) { path, matchers ->
                if (matchers.isEmpty())    getSubStaticRouter(controllerRouter, path)
                else {
                    getSubDynamicRouter(
                        controllerRouter,
                        if (path.isEmpty() && matchers.size == 1 && matchers[0].second == ".*")
                            NamedVariableMatcher(matchers[0].first)
                        else RegexMatcher(path, matchers.map { item -> item.first }.toTypedArray())
                    )
                }
            }
        }

        return ControllerProcessFlowInfo(channels, controllerRouter)
    }


    override fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerProcessFlowInfo<CTX, ROT>,
        controllerClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ActionProcessFlowInfo<CTX, ROT>? {
        TODO("Not yet implemented")
    }

    abstract fun getSubStaticRouter(router: ROT, subPath: String): ROT
    abstract fun getSubDynamicRouter(router: ROT, matcher: RouterMatcher<CTX>): ROT
    abstract fun controllerChannel(annotation: Annotation?, controllerClass: Class<*>): List<String>
    abstract fun actionInfo(controllerChannel: List<String>, actionMethod: Method): Pair<String, List<String>>?
    abstract fun createMethodInvoker(
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): PI?

    abstract fun createCatchMethodInvoker(
        throwableType: Class<out Throwable>,
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): PI?

}
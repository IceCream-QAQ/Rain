package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Path
import com.IceCreamQAQ.Yu.annotation.With
import com.IceCreamQAQ.Yu.controller.router.*
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


abstract class NewControllerLoader : Loader {

    private val logger = LoggerFactory.getLogger(NewControllerLoader::class.java)

    @Inject
    private lateinit var context: YuContext

    protected abstract fun createMethodInvoker(obj: Any, method: Method): NewMethodInvoker
    protected abstract fun createActionInvoker(level: Int, actionMethod: Method): NewActionInvoker

    override fun load(items: Map<String, LoadItem>) {
        val rootRouters = HashMap<String, NewRouterImpl>()
        for (item in items.values) {
            val clazz = item.type
            val name = clazz.getAnnotation(Named::class.java)?.value
                    ?: item.loadBy::class.java.interfaces[0].getAnnotation(Named::class.java)?.value ?: continue
            val rootRouter = rootRouters[name] ?: {
                val r = NewRouterImpl(1)
                rootRouters[name] = r
                r
            }()

            controllerToRouter(context[clazz] ?: continue, rootRouter)
        }

        for ((k, v) in rootRouters) {
            context.putBean(NewRouter::class.java, k, v)
        }
    }


    fun getMethods(methods: MutableList<Method>, clazz: Class<*>) {
        methods.addAll(clazz.methods)
        getMethods(methods, clazz.superclass ?: return)
    }

    val p = Pattern.compile("\\{(.*?)}")
    fun controllerToRouter(instance: Any, rootRouter: NewRouterImpl) {
        val controllerClass = instance::class.java

        val controllerRouter = getRouterByPathString(rootRouter, controllerClass.getAnnotation(Path::class.java)?.value, 0).router

        val allMethods = ArrayList<Method>()
        getMethods(allMethods, controllerClass)
        val with = controllerClass.getAnnotation(With::class.java)?.value
        if (with != null)
            for (kClass in with) {
                getMethods(allMethods, kClass.java)
            }

        val methods = controllerClass.methods
        val befores = HashMap<Before, NewMethodInvoker>()
        for (method in allMethods) {
            val before = method.getAnnotation(Before::class.java)
            if (before != null) {
                val beforeInvoker = createMethodInvoker(instance, method)
                befores[before] = (beforeInvoker)
            }
        }
//        val before = befores.toTypedArray()
        for (method in methods) {
            val action = method.getAnnotation(Action::class.java)
            if (action != null) {
                val actionMethodName = method.name

                val path = action.value
                val aa = getActionRouter(path, controllerRouter, rootRouter)
                val actionRootRouter = aa.router
                val actionPath = aa.path

                val methodInvoker = createMethodInvoker(instance, method)
                val actionInvoker = createActionInvoker(actionRootRouter.level + 1, method)
                actionInvoker.invoker = methodInvoker

                val abs = ArrayList<NewMethodInvoker>()
                w@ for ((before, invoker) in befores) {
                    if (before.except.size != 1 || before.except[0] != "") for (s in before.except) {
                        if (s == actionMethodName) continue@w
                    }
                    if (before.only.size != 1 || before.only[0] != "") for (s in before.only) {
                        if (s != actionMethodName) continue@w
                    }
                    abs.add(invoker)
                }

                actionInvoker.befores = abs.toTypedArray()

                val mi = getMatchItem(actionPath, actionInvoker)

                if (mi == null) actionRootRouter.noMatch[actionPath] = actionInvoker
                else actionRootRouter.needMath.add(mi)
            }
        }

    }

    fun getMatchItem(pathString: String, nextRouter: NewRouterImpl): MatchItem? {
        var path = pathString
        return if (path.startsWith("\\") && path.endsWith("\\"))
            MatchItem(
                    false,
                    path.substring(1).substring(0, path.length - 2),
                    null,
                    nextRouter
            )
        else {
            val pvs = ArrayList<String>()
            val m = p.matcher(path)
            while (m.find()) pvs.add(m.group(1))
            if (pvs.size == 0) null
            else {
                val matchNames = ArrayList<String>()
                for (pv in pvs) {
                    if (pv.contains(":")) {
                        val s = pv.split(":")
                        path = path.replace("{$pv}", "(${s[1]})")
                        matchNames.add(s[0])
                    } else {
                        path = path.replace("{$pv}", "(.*)")
                        matchNames.add(pv)
                    }
                }
                MatchItem(
                        true,
                        "^$path",
                        matchNames.toTypedArray(),
                        nextRouter
                )
            }
        }
    }

    data class ActionRouterAndPath(val router: NewRouterImpl, val path: String)

    fun getActionRouter(pathString: String, controllerRouter: NewRouterImpl, rootRouter: NewRouterImpl): ActionRouterAndPath {
        var path = pathString
        return if (path.contains("/")) {
            val router = if (path[0] == '/') {
                path = path.substring(1)
                rootRouter
            } else {
                controllerRouter
            }
            getRouterByPathString(router, path, 1)
        } else ActionRouterAndPath(controllerRouter, pathString)
    }

    fun getRouter(router: NewRouterImpl, name: String): NewRouterImpl {
        val level = router.level + 1
        var nextRouter = NewRouterImpl(level)
        val mi = getMatchItem(name, nextRouter)

        if (mi == null) {
            val r = router.noMatch[name]
            if (r !is NewRouterImpl) router.noMatch[name] = nextRouter
            else nextRouter = r
        } else {
            router.needMath.add(mi)
        }

        return nextRouter
    }

    fun getRouterByPathString(router: NewRouterImpl, pathString: String?, lessLevel: Int): ActionRouterAndPath {
        if (pathString == null) return ActionRouterAndPath(router, "")
        val paths = pathString.split("/".toRegex())
        var finishRouter = router
        val length = paths.size - lessLevel
        for (i in 0 until length) {
            val path = paths[i]
            finishRouter = getRouter(finishRouter, path)
        }
        return ActionRouterAndPath(finishRouter, paths.last())
    }

}

open class NewControllerLoaderImpl : NewControllerLoader() {
    override fun createMethodInvoker(obj: Any, method: Method): NewMethodInvoker = NewReflectMethodInvoker(method, obj)

    override fun createActionInvoker(level: Int, actionMethod: Method): NewActionInvoker = NewActionInvoker(level)
}
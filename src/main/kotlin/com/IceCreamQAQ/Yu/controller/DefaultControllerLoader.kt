package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Path
import com.IceCreamQAQ.Yu.annotation.Synonym
import com.IceCreamQAQ.Yu.controller.router.DefaultRouter
import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.MethodInvoker
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import java.lang.reflect.Method
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@Deprecated("已经弃用")
abstract class DefaultControllerLoader : Loader {

    @Inject
    private lateinit var logger: AppLogger

    @Inject
    private lateinit var context: YuContext

    override fun load(items: Map<String, LoadItem>) {
        val rootRouters = HashMap<String, DefaultRouter>()
        for (item in items.values) {
            val clazz = item.type
            val name = clazz.getAnnotation(Named::class.java)?.value
                    ?: item.loadBy::class.java.interfaces[0].getAnnotation(Named::class.java)?.value ?: continue
            val rootRouter = rootRouters[name] ?: {
                val r = DefaultRouter(0)
                rootRouters[name] = r
                r
            }()

            controllerToRouter_(context[clazz] ?: continue, rootRouter)
        }

        for ((k, v) in rootRouters) {
            context.putBean(v, k)
        }
    }

    @Throws(java.lang.Exception::class)
    protected abstract fun createMethodInvoker_(obj: Any, method: Method): MethodInvoker

    protected abstract fun createActionInvoker_(level: Int, actionMethod: Method): DefaultActionInvoker

    fun controllerToRouter_(instance: Any, rootRouter: DefaultRouter) {
        val controllerClass = instance::class.java

        val controllerRouter = getRouterByPathString(rootRouter, controllerClass.getAnnotation(Path::class.java)?.value, 0)

        val methods = controllerClass.methods
        val befores = ArrayList<MethodInvoker>()
        for (method in methods) {
            val before = method.getAnnotation(Before::class.java)
            if (before != null) {
                logger.logInfo("YuQ Loader", "Before " + method.name + " 正在载入。")
                val beforeInvoker = createMethodInvoker_(instance, method)
                befores.add(beforeInvoker)
            }
        }
        val before = befores.toTypedArray()
        for (method in methods) {
            val action = method.getAnnotation(Action::class.java)
            if (action != null) {
                logger.logInfo("YuQ Loader", "Action " + method.name + " 正在载入。")
                val path = action.value
                val actionRootRouter = getActionRouter(path, controllerRouter, rootRouter)
                val methodInvoker = createMethodInvoker_(instance, method)
                val actionInvoker = createActionInvoker_(actionRootRouter.level + 1, method)
                actionInvoker.invoker = methodInvoker
                actionInvoker.befores = before
                if (path.startsWith("{") && path.endsWith("}"))
                    actionRootRouter.needMatch[path.substring(1).substring(0, path.length - 2)] = actionInvoker
                else actionRootRouter.routers[path] = actionInvoker

                val s = method.getAnnotation(Synonym::class.java)?:continue
                val paths = s.value
                for (path in paths) {
                    val actionRootRouter = getActionRouter(path, controllerRouter, rootRouter)
                    if (path.startsWith("{") && path.endsWith("}"))
                        actionRootRouter.needMatch[path.substring(1).substring(0, path.length - 2)] = actionInvoker
                    else actionRootRouter.routers[path] = actionInvoker
                }
            }
        }
        logger.logInfo("YuQ Loader", "共有 " + befores.size + " 个 Before 被载入。")
    }

    fun getActionRouter(pathString: String, controllerRouter: DefaultRouter, rootRouter: DefaultRouter): DefaultRouter {
        var path = pathString
        var actionRootRouter: DefaultRouter
        if (path.contains("/")) {
            if (path[0] == '/') {
                path = path.substring(1)
                actionRootRouter = rootRouter
            } else {
                actionRootRouter = controllerRouter
            }
            actionRootRouter = getRouterByPathString(actionRootRouter, path, 1)
        } else {
            actionRootRouter = controllerRouter
        }
        return actionRootRouter
    }

//    @Throws(Exception::class)
//    fun controllerToRouter(instance: Any, rootRouter: DefaultRouter) {
//        val controller = instance::class.java
//        val fileName = controller.name.replace(".", "/") + ".class"
//        val iStream = controller.classLoader.getResourceAsStream(fileName)
//        val cr = ClassReader(iStream)
//        val node = ClassNode()
//        cr.accept(node, 0)
//        val methodMap = HashMap<String, MethodNode?>()
//        val cvMethods = node.methods as List<MethodNode?>
//        for (method in cvMethods) {
//            methodMap[method!!.name] = method
//        }
//        val paths = controller.getAnnotationsByType(Path::class.java)
//        var controllerRouter: DefaultRouter
//        when {
//            paths.isEmpty() -> controllerRouter = rootRouter
//            paths.size == 1 -> {
//                val pathString: String = paths[0].value
//                controllerRouter = getRouterByPathString(rootRouter, pathString, 0)
//            }
//            else -> {
//                controllerRouter = rootRouter
//                for (path in paths) {
//                    controllerRouter = getRouter(controllerRouter, path.value)
//                }
//            }
//        }
//
//        val methods = controller.methods
//        val befores = ArrayList<MethodInvoker>()
//        for (method in methods) {
//            val before = method.getAnnotation(Before::class.java)
//            if (before != null) {
//                logger.logInfo("YuQ Loader", "Before " + method.name + " 正在载入。")
//                val beforeInvoker: MethodInvoker = createMethodInvoker(instance, method, methodMap[method.name]!!)
//                befores.add(beforeInvoker)
//            }
//        }
//        val before = befores.toTypedArray()
//        for (method in methods) {
//            val action = method.getAnnotation(Action::class.java)
//            if (action != null) {
//                logger.logInfo("YuQ Loader", "Action " + method.name + " 正在载入。")
//                var path: String = action.value
//                var actionRootRouter: DefaultRouter
//                if (path.contains("/")) {
//                    if ("/" == path.substring(0, 1)) {
//                        path = path.substring(1)
//                        actionRootRouter = rootRouter
//                    } else {
//                        actionRootRouter = controllerRouter
//                    }
//                    actionRootRouter = getRouterByPathString(actionRootRouter, path, 1)
//                } else {
//                    actionRootRouter = controllerRouter
//                }
//                val methodInvoker = createMethodInvoker(instance, method, methodMap[method.name]!!)
//                val actionInvoker = createActionInvoker(method)
//                actionInvoker.invoker = methodInvoker
//                actionInvoker.befores = before
//                actionRootRouter.routers[path] = actionInvoker
//            }
//        }
//        logger.logInfo("YuQ Loader", "共有 " + befores.size + " 个 Before 被载入。")
//    }
//
//    @Throws(java.lang.Exception::class)
//    protected abstract fun createMethodInvoker(obj: Any, method: Method, methodNode: MethodNode): MethodInvoker
//
//    protected abstract fun createActionInvoker(method: Method): DefaultActionInvoker

    private fun getRouter(router: DefaultRouter, name: String): DefaultRouter {
        var nextRouter = router.routers[name]
        if (nextRouter !is DefaultRouter) {
            val level = router.level + 1
            nextRouter = DefaultRouter(level)
            router.routers[name] = nextRouter
        }
        return nextRouter
    }

    private fun getRouterByPathString(router: DefaultRouter, pathString: String?, lessLevel: Int): DefaultRouter {
        if (pathString == null) return router
        val paths = pathString.split("/".toRegex())
        var finishRouter = router
        val length = paths.size - lessLevel
        for (i in 0 until length) {
            val path = paths[i]
            finishRouter = getRouter(finishRouter, path)
        }
        return finishRouter
    }

}
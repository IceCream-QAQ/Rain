package com.IceCreamQAQ.Yu.controller.default

import com.IceCreamQAQ.Yu.annotation.Path
import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.controller.base.BaseCatchInvoker
import com.IceCreamQAQ.Yu.controller.base.BaseControllerLoader
import java.lang.reflect.Method

//open class DefaultControllerLoader : BaseControllerLoader() {
//
//    override val rootRouter: RootRouter = DefaultRootRouter()
//    override val defaultMethods: Array<String> = arrayOf("GET", "POST")
//
//    open val defaultSplit: Array<String> = arrayOf("/")
//
//    override fun createMethodInvoker(instance: Any, method: Method): MethodInvoker =
//        DefaultReflectMethodInvoker(method, instance)
//
//    override fun createCatchInvoker(instance: Any, method: Method, errorType: Class<out Throwable>): CatchInvoker =
//        DefaultCatchInvoker(errorType, DefaultReflectMethodInvoker(method, instance))
//
////    override fun getControllerRouter(controller: Class<*>): Router {
////        val path = controller.getAnnotation(Path::class.java)?.value ?: return rootRouter.router
////        val ps = path.split(*defaultSplit)
////
////    }
////
////    open fun pathToRouterPath(path: String): Triple<String, String?, Array<String>?> {
////        if (path.startsWith("\\") && path.endsWith("\\")) {
////            val rp = path.substring(1, path.length - 1)
////            return Triple(rp, rp, null)
////        }
////
////    }
//
//    override fun checkAction(
//        method: Method,
//        controllerRouter: Router,
//        enableMethods: Array<String>,
//        interceptorInfo: InterceptorInfo
//    ): ActionInfo? {
//        TODO("Not yet implemented")
//    }
//
//    override fun loadSuccess() {
//        context.putBean(DefaultRootRouter::class.java, "Default", rootRouter)
//    }
//
//}
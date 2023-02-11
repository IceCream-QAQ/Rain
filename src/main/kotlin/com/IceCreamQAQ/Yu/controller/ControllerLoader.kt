package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.allMethod
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.isBean
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import com.IceCreamQAQ.Yu.named
import java.lang.reflect.Method

abstract class ControllerLoader<T : ActionContext, R : Router, RootInfo : RootRouterProcessFlowInfo<T, R>>(
    val context: YuContext
) : Loader {


    override fun load(items: Collection<LoadItem>) {

        items.forEach {
            if (!it.clazz.isBean) return@forEach

            val rootRouter = findRootRouter(it.clazz.named) ?: return@forEach
            val controllerFlow = controllerInfo(rootRouter) ?: return@forEach

            it.clazz.allMethod.forEach { m ->

            }
        }

        postLoad()
    }

    abstract fun postLoad()


    abstract fun findRootRouter(name: String): RootInfo?
    abstract fun controllerInfo(root: RootInfo): ControllerProcessFlowInfo<T>?
    abstract fun actionToRouter(
        rootRouter: RootInfo,
        controllerRouter: Router,
        actionMethod: Method
    ): ProcessInvoker<T>?

}
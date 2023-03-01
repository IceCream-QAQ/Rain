package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.allMethod
import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.After
import com.IceCreamQAQ.Yu.annotation.Before
import com.IceCreamQAQ.Yu.annotation.Catch
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.isBean
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import com.IceCreamQAQ.Yu.named
import java.lang.reflect.Method

abstract class ControllerLoader<CTX : ActionContext, ROT : Router, RootInfo : RootRouterProcessFlowInfo<CTX, ROT>>(
    val context: YuContext
) : Loader {

    /** Controller 处理流程
     * 上一代路由
     * Context -> RootRouter -> Router -> ActionInvoker ({ Befores -> Action -> Afters } Catchs)
     * 新路由
     * Context -> RootRouter -> ChannelRouter -> Router ->
     *     ActionInvoker ({ BeforeProcesses -> Action -> AfterProcesses} CatchProcesses)
     *
     * 与上一代不同的是，新路由在根路由后新增了 ChannelRouter 作为通道分流。
     * 方便在同一根路由下直接构建 GET/POST 等不同通道的路由分流。
     * 上代路由如果想实现通道分流则需要创建多个 RootRouter。
     *
     * 同时对上一代 Action 处理链路中的拦截器进行改进，改进为流程。
     * 保留上一代 Before，After，Catch 特性的同时，引入外部流程。
     * 通过在 Controller类 与 Action方法 上标记具有 声明外部流程注解的注解，来声明引入了一个 外部流程。
     * 在载入时扫描相关注解，注册相关 Factory，并且将 外部流程 与 Before 等 本地流程混编进入处理链路。
     * 使得 Controller 与 Action 运行检查可以更加简便。
     */
    override fun load(items: Collection<LoadItem>) {

        items.forEach {
            if (!it.clazz.isBean) return@forEach
            val type = it.clazz

            // 为后续兼容多例预留支持空间，但暂时不做。
            val instance = context.getBean(it.clazz) ?: return@forEach
            val getter = ControllerInstanceGetter { instance }

            val rootRouter = findRootRouter(it.clazz.named) ?: return@forEach
            val controllerFlow = controllerInfo(rootRouter, it.annotation, type, getter) ?: return@forEach

            // 后续应该做出扫描 Controller类 所有注解，并扫描注解是否具有 BeforeBy 等注解，然后根据相应工厂类，创建 Before 等 Process。


            it.clazz.allMethod.forEach { m ->

                m.annotation<Before> {
                    makeBefore(this, type, m, getter)
                        ?.let { p -> controllerFlow.beforeProcesses.add(p) }
                }
                m.annotation<After> {
                    makeAfter(this, type, m, getter)
                        ?.let { p -> controllerFlow.beforeProcesses.add(p) }
                }
                m.annotation<Catch> {
                    makeCatch(this, type, m, getter)
                        ?.let { p -> controllerFlow.beforeProcesses.add(p) }
                }


                makeAction(rootRouter, controllerFlow, type, m, getter)
                    ?.let { action ->
                        controllerFlow.actions.add(action)
                        // 后续应该做出扫描 Action方法 所有注解，并扫描注解是否具有 BeforeBy 等注解，然后根据相应工厂类，创建 Before 等 Process。
                    }
            }
        }

        postLoad()
    }

    abstract fun postLoad()


    abstract fun findRootRouter(name: String): RootInfo?
    abstract fun controllerInfo(
        root: RootInfo,
        annotation: Annotation?,
        controllerClass: Class<*>,
        instanceGetter: ControllerInstanceGetter
    ): ControllerProcessFlowInfo<CTX,ROT>?

    abstract fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerProcessFlowInfo<CTX,ROT>,
        controllerClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ActionProcessFlowInfo<CTX>?

    abstract fun makeBefore(
        beforeAnnotation: Before,
        controllerClass: Class<*>,
        beforeMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>?

    abstract fun makeAfter(
        afterAnnotation: After,
        controllerClass: Class<*>,
        afterMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>?

    abstract fun makeCatch(
        catchAnnotation: Catch,
        controllerClass: Class<*>,
        catchMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>?

}

private fun <T> Method.findAnnotation(block: () -> Unit) {

}

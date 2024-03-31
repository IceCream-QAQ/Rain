package rain.controller

import rain.api.di.DiContext
import rain.api.di.named
import rain.api.loader.LoadItem
import rain.api.loader.Loader
import rain.controller.annotation.*
import rain.function.*
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaGetter

abstract class ControllerLoader<CTX : ActionContext, ROT : Router, RootInfo : RootRouterProcessFlowInfo<CTX, ROT>>(
    val context: DiContext
) : Loader {

    /** Controller 处理流程
     * 上一代路由
     * Context -> RootRouter -> Router -> ActionInvoker ({ Befores -> Action -> Afters } Catchs)
     * 新路由
     * Context -> RootRouter -> Router ->
     *     ActionInvoker ({ BeforeProcesses -> Action -> AfterProcesses} CatchProcesses)
     *
     * 同上一代相比，分离的 ActionInvoker 继承自 Router 的结构。
     * 将 Action 直接置于 Router 之下，并且单一路由允许绑定多个 Action。
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

            fun AnnotatedElement.checkProcessBy() {
                val isMethod = this is Method
                this.annotations.forEach { an ->
                    an.annotationAnnotation<ProcessBy>().forEach { pb ->
                        val pbt = pb.value.java as Class<out ProcessProvider<CTX>>
                        val pbi = context.getBean(pbt) ?: error("无法获取 ProcessBy: ${pbt.name} 的实例！")

                        fun <T : Annotation> checkProcess(
                            field: KProperty1<ProcessFlowInfo<CTX>, MutableList<ProcessInfo<CTX>>>,
                            annotationField: KProperty1<T, Int>
                        ) {
                            val annotationClass = annotationField.javaGetter!!.declaringClass as Class<T>
                            val can = pbt.getAnnotation(annotationClass) ?: return
                            val w = annotationField.get(can)
                            val invoker =
                                pbi(an, can, type as Class<Any>, instance, if (isMethod) this as Method else null)
                            field.get(controllerFlow)
                                .add(
                                    ProcessInfo(
                                        w,
                                        emptyArray(),
                                        if (isMethod) arrayOf((this as Method).name) else emptyArray(),
                                        invoker
                                    )
                                )

                        }
                        checkProcess(ProcessFlowInfo<CTX>::beforeProcesses, Before::weight)
                        checkProcess(ProcessFlowInfo<CTX>::afterProcesses, After::weight)
                        checkProcess(ProcessFlowInfo<CTX>::catchProcesses, Catch::weight)
                    }
                }
            }
            type.checkProcessBy()

            type.allMethod.forEach { m ->

                fun ProcessInfo<CTX>.checkGlobal(
                    field: KProperty1<ProcessFlowInfo<CTX>, MutableList<ProcessInfo<CTX>>>
                ) {
                    val isGlobal = m.hasAnnotation<Global>()
                    let {
                        if (isGlobal) field.get(rootRouter)
                        else field.get(controllerFlow)
                    }.add(this)
                }

                m.annotation<Before> {
                    makeBefore(this, type, m, getter)
                        ?.checkGlobal(ProcessFlowInfo<CTX>::beforeProcesses)
                }
                m.annotation<After> {
                    makeAfter(this, type, m, getter)
                        ?.checkGlobal(ProcessFlowInfo<CTX>::afterProcesses)
                }
                m.annotation<Catch> {
                    makeCatch(this, type, m, getter)
                        ?.checkGlobal(ProcessFlowInfo<CTX>::catchProcesses)
                }


                makeAction(rootRouter, controllerFlow, type, m, getter)
                    ?.let { action ->
                        controllerFlow.actions.add(action)
                        m.checkProcessBy()
                    }

            }
            rootRouter.controllers.add(controllerFlow)
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
    ): ControllerProcessFlowInfo<CTX, ROT>?

    abstract fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerProcessFlowInfo<CTX, ROT>,
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

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

abstract class ControllerLoader<
        CTX : ActionContext,
        ROT : Router,
        RootInfo : RootRouterProcessFlowInfo<CTX, ROT, AI>,
        AI : ActionInvoker<CTX>,
        ControllerInfo : ControllerProcessFlowInfo<CTX, ROT, AI>
        >(
    val context: DiContext
) : Loader {

    private fun checkProcessFilter(
        provideAnnotation: Annotation,
        functionAnnotation: Annotation,
        controllerFlow: ControllerProcessFlowInfo<CTX, ROT, AI>,
        controllerInstance: Any,
        source: AnnotatedElement
    ): Array<String> {
        val pfb = source.annotation<ProcessFilterBy>() ?: return emptyArray()
        val processFilterProviderClass = pfb.value.java
        val pfp = context.getBean(processFilterProviderClass)
            ?: error("无法获取 ProcessFilterProvider: ${processFilterProviderClass.name} 的实例！")

        return controllerFlow.actions.filter {
            !pfp(
                provideAnnotation,
                functionAnnotation,
                controllerFlow.controllerClass as Class<Any>,
                controllerInstance,
                it.actionMethod
            )
        }.map { it.actionMethod.nameAtParams }
            .toTypedArray()
    }

    open fun AnnotatedElement.processFilter(): ProcessFilter? {
        val pfb = annotation<ProcessFilterBy>() ?: return null
        val processFilterProviderClass = pfb.value.java
        return context.getBean(processFilterProviderClass)
            ?: error("无法获取 ProcessFilterProvider: ${processFilterProviderClass.name} 的实例")
    }

    /** Controller 处理流程
     * 上一代路由
     * Context -> RootRouter -> Router -> ActionInvoker ({ Befores -> Action -> Afters } Catchs)
     * 新路由
     * Context -> RootRouter -> Router ->
     *     ActionInvoker ({ BeforeProcesses -> Action -> AfterProcesses} CatchProcesses)
     *
     * 同上一代相比，分离的 ActionInvoker 继承自 Router 的结构。
     * 将 Action 直接置于 Router 之下，并且 Action 是直接应用于当前路由结构。
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

                            field.get(controllerFlow).add(
                                ProcessInfo(
                                    an,
                                    can,
                                    this.processFilter(),
                                    w,
                                    emptyArray(),
                                    if (isMethod) arrayOf(this.nameAtParams) else emptyArray(),
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

                fun ProcessInfo<CTX>.checkFilter(annotation: Annotation): ProcessInfo<CTX> {
                    val except = checkProcessFilter(
                        annotation,
                        annotation,
                        controllerFlow,
                        instance,
                        type
                    )
                    return if (except.isEmpty()) this
                    else this.copy(except = this.except + except)
                }

                m.annotation<Before> {
                    makeBefore(controllerFlow, this, type, m, getter)
                        ?.checkGlobal(ProcessFlowInfo<CTX>::beforeProcesses)
                }
                m.annotation<After> {
                    makeAfter(controllerFlow, this, type, m, getter)
                        ?.checkGlobal(ProcessFlowInfo<CTX>::afterProcesses)
                }
                m.annotation<Catch> {
                    makeCatch(controllerFlow, this, type, m, getter)
                        ?.checkGlobal(ProcessFlowInfo<CTX>::catchProcesses)
                }

                makeAction(rootRouter, controllerFlow, type, m, getter)
                    ?.let { action ->
                        m.checkProcessBy()
                        controllerFlow.actions.add(action)
                    }
            }


            rootRouter.controllers.add(controllerFlow)
        }

        postLoad()
    }

    // 加载后处理
    abstract fun postLoad()

    open fun buildRootInfo(info: RootInfo): RootRouter<CTX, ROT, AI> {
        val actions = info.controllers.flatMap { ci ->
            ci.actions.map { ac ->
                val befores = ArrayList<ProcessInfo<CTX>>()
                val afters = ArrayList<ProcessInfo<CTX>>()
                val catchs = ArrayList<ProcessInfo<CTX>>()

                fun checkProcesses(
                    processes: List<ProcessInfo<CTX>>,
                    target: MutableList<ProcessInfo<CTX>>,
                ) {
                    processes.forEach {
                        if (checkProcessSupportAction(it, ci.controllerClass, ci.controllerInstance, ac.actionMethod))
                            target.add(it)
                    }
                }

                checkProcesses(info.beforeProcesses, befores)
                checkProcesses(ci.beforeProcesses, befores)
                checkProcesses(info.afterProcesses, afters)
                checkProcesses(ci.afterProcesses, afters)
                checkProcesses(info.catchProcesses, catchs)
                checkProcesses(ci.catchProcesses, catchs)

                fun MutableList<ProcessInfo<CTX>>.doProcesses(): Array<ProcessInvoker<CTX>> {
                    sortBy { it.priority }
                    return map { it.invoker }.toTypedArray()
                }

                ac.creator(befores.doProcesses(), afters.doProcesses(), catchs.doProcesses())
            }
        }
        return buildRootRouter(info.router, actions)
    }

    open fun buildRootRouter(router: ROT, actions: List<AI>) = RootRouter(router, actions)

    open fun checkProcessSupportAction(
        process: ProcessInfo<CTX>,
        controllerClass: Class<*>,
        controllerInstance: Any,
        actionMethod: Method,
    ): Boolean {
        process.filter?.invoke(
            process.providerAnnotation,
            process.functionAnnotation,
            controllerClass as Class<Any>,
            controllerInstance,
            actionMethod
        )?.let { if (!it) return false }
        val methodName = actionMethod.name
        val paramTypeNames by lazy { actionMethod.parameterTypes.map { it.name } }
        val paramTypeSimpleNames by lazy { actionMethod.parameterTypes.map { it.simpleName } }
        fun check(script: String): Boolean {
            if (script.contains('@').not()) return script == methodName
            val parts = script.split('@', limit = 2)
            val name = parts[0]
            if (name != methodName) return false
            if (actionMethod.parameters.size == 0 && parts[1].isEmpty()) return true
            val params = parts[1].split(",")
            if (params.size != actionMethod.parameterTypes.size) return false
            params.forEachIndexed { index, s ->
                if (s.isEmpty()) return false
                run {
                    if (s[0] == '.')
                        s.substring(1) != paramTypeSimpleNames[index]
                    else s != paramTypeNames[index]
                }.let { if (!it) return false }
            }
            return true
        }
        if (process.only.isNotEmpty()) if (process.only.none { check(it) }) return false
        if (process.except.isNotEmpty()) if (process.except.any { check(it) }) return false
        return true
    }

    /**
     * 查找具有给定名称的根路由器。
     *
     * @param name 要查找的根路由器的名称。
     * @return 如果找到，则返回根路由器，否则返回 null。
     */
    abstract fun findRootRouter(name: String): RootInfo?

    /**
     * 创建一个控制器过程流信息对象。
     *
     * @param root 根路由器。
     * @param annotation 控制器上的注解。
     * @param controllerClass 控制器的类。
     * @param controllerInstance 控制器实例的获取器。
     * @return 创建的控制器过程流信息对象。
     */
    abstract fun controllerInfo(
        root: RootInfo,
        annotation: Annotation?,
        controllerClass: Class<*>,
        controllerInstance: Any,
    ): ControllerInfo?

    /** 尝试对目标函数创建 Action
     * 如果目标是正确的 Action函数，则返回一个 Action 过程流信息对象。
     * 如果不是，则返回 Null。
     *
     * @param rootRouter 根路由器。
     * @param controllerFlow 控制器过程流信息。
     * @param controllerClass 控制器的类。
     * @param actionMethod 动作方法。
     * @param instanceGetter 控制器实例的获取器。
     * @return 创建的动作过程流信息对象。
     */
    abstract fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerInfo,
        controllerClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ActionCreator<CTX, AI>?

    /** 创建一个过程调用器。
     *
     * @param processClass 过程类。
     * @param processMethod 过程方法。
     * @param processInstance 过程实例的获取器。
     * @return 创建的过程调用器。
     */
    abstract fun makeProcessInvoker(
        controllerInfo: ControllerInfo,
        processClass: Class<*>,
        processMethod: Method,
        processInstance: ControllerInstanceGetter,
    ): ProcessInvoker<CTX>?

    /**
     * 为 before 创建一个过程信息对象。
     *
     * @param beforeAnnotation before 注解。
     * @param controllerClass 控制器的类。
     * @param beforeMethod before 方法。
     * @param instanceGetter 控制器实例的获取器。
     * @return 创建的过程信息对象。
     */
    open fun makeBefore(
        controllerInfo: ControllerInfo,
        beforeAnnotation: Before,
        controllerClass: Class<*>,
        beforeMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        makeProcessInvoker(controllerInfo, controllerClass, beforeMethod, instanceGetter)
            ?.let {
                ProcessInfo(
                    beforeAnnotation,
                    beforeAnnotation,
                    beforeMethod.processFilter(),
                    beforeAnnotation.weight,
                    beforeAnnotation.except,
                    beforeAnnotation.only,
                    it
                )
            }

    /**
     * 为 after 创建一个过程信息对象。
     *
     * @param afterAnnotation after 注解。
     * @param controllerClass 控制器的类。
     * @param afterMethod after 方法。
     * @param instanceGetter 控制器实例的获取器。
     * @return 创建的过程信息对象。
     */
    open fun makeAfter(
        controllerInfo: ControllerInfo,
        afterAnnotation: After,
        controllerClass: Class<*>,
        afterMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        makeProcessInvoker(controllerInfo, controllerClass, afterMethod, instanceGetter)
            ?.let {
                ProcessInfo(
                    afterAnnotation,
                    afterAnnotation,
                    afterMethod.processFilter(),
                    afterAnnotation.weight,
                    afterAnnotation.except,
                    afterAnnotation.only,
                    it
                )
            }

    /**
     * 为 catch 创建一个过程信息对象。
     *
     * @param catchAnnotation catch 注解。
     * @param controllerClass 控制器的类。
     * @param catchMethod catch 方法。
     * @param instanceGetter 控制器实例的获取器。
     * @return 创建的过程信息对象。
     */
    open fun makeCatch(
        controllerInfo: ControllerInfo,
        catchAnnotation: Catch,
        controllerClass: Class<*>,
        catchMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInfo<CTX>? =
        makeProcessInvoker(controllerInfo, controllerClass, catchMethod, instanceGetter)
            ?.let {
                ProcessInfo(
                    catchAnnotation,
                    catchAnnotation,
                    catchMethod.processFilter(),
                    catchAnnotation.weight,
                    catchAnnotation.except,
                    catchAnnotation.only,
                    it
                )
            }

}

package rain.controller.dss

import rain.api.di.DiContext
import rain.controller.*
import rain.controller.annotation.After
import rain.controller.annotation.Before
import rain.controller.annotation.Catch
import rain.controller.annotation.Path
import rain.controller.dss.router.*
import rain.function.annotation
import java.lang.reflect.Method
import kotlin.reflect.KProperty1

/** 动静分离 ControllerLoader
 * 这是一个简单的 Controller 实现，用以技术性验证。
 * 这个简单的实现提供了基础的 Router 与 Action 实现，如果业务合适，可以直接基于本实现开展业务。
 *
 * 路由做了基础的静态匹配与动态匹配分离。
 */
abstract class DssControllerLoader<CTX : PathActionContext, ROT : DssRouter<CTX>, RootInfo : RootRouterProcessFlowInfo<CTX, ROT>>(
    context: DiContext
) : ControllerLoader<CTX, ROT, RootInfo>(context) {

    open fun margePath(path: Array<String>): String =
        path.joinToString("/")

    open fun splitPath(path: String): Array<String> =
        path.split("/").toTypedArray()


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

    open fun makePathMatcher(path: String): Pair<String, ArrayList<Pair<String, String?>>> {
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

        return (if (locationFlag) realPathBuilder.toString() else "") to matchList
    }

    override fun controllerInfo(
        root: RootInfo,
        annotation: Annotation?,
        controllerClass: Class<*>,
        instanceGetter: ControllerInstanceGetter
    ): ControllerProcessFlowInfo<CTX, ROT>? {
        val channels = controllerChannel(annotation, controllerClass)
        var controllerRouter = root.router

        val pathList = ArrayList<String>()
        var pathClass: Class<*>? = controllerClass
        while (pathClass != null) {
            pathClass.annotation<Path> { pathList.add(value) }
            pathClass = pathClass.superclass
        }

        val margePathList = ArrayList<String>()
        for (it in pathList) {
            margePathList.add(it)
            if (it.startsWith("/")) break
        }

        val path = if (margePathList.isNotEmpty()) splitPath(margePath(margePathList.reversed().toTypedArray()))
        else emptyArray()

        if (path.isNotEmpty())
            path.forEach {
                controllerRouter = makePathMatcher(it).let { (path, matchers) ->
                    if (matchers.isEmpty()) getSubStaticRouter(controllerRouter, path)
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



        return ControllerProcessFlowInfo(controllerClass, channels, controllerRouter)
    }


    override fun makeAction(
        rootRouter: RootInfo,
        controllerFlow: ControllerProcessFlowInfo<CTX, ROT>,
        controllerClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ActionProcessFlowInfo<CTX>? {
        var (pathString, channels) = actionInfo(controllerFlow.controllerChannels, actionMethod) ?: return null
        val rootFlag = if (pathString.isNotEmpty() && pathString.first() == '/') {
            pathString = pathString.substring(1)
            true
        } else false


        val actionName = actionMethod.name

        val paths = pathString.split("/")

        val actionRouter = (if (rootFlag) rootRouter.router else controllerFlow.controllerRouter)

        val actionMatchers = paths.map {
            makePathMatcher(it).let { (path, matchers) ->
                if (matchers.isEmpty()) StaticActionMatcher<CTX>(it)
                else if (path.isEmpty() && matchers.size == 1 && matchers[0].second == ".*")
                    NamedVariableMatcher(matchers[0].first)
                else RegexMatcher(path, matchers.map { item -> item.first }.toTypedArray())

            }
        }

        val actionFlow = ActionProcessFlowInfo<CTX>(controllerClass, actionMethod)

        fun checkPf(property: KProperty1<ProcessFlowInfo<CTX>, MutableList<ProcessInfo<CTX>>>): Array<ProcessInvoker<CTX>> =
            ArrayList<ProcessInfo<CTX>>()
                .apply {
                    val checkPi = { it: ProcessInfo<CTX> ->
                        if (actionName !in it.except && (it.only.isEmpty() || actionName in it.only)) add(it)
                    }
                    property.get(rootRouter).forEach(checkPi)
                    property.get(controllerFlow).forEach(checkPi)
                    property.get(actionFlow).forEach(checkPi)
                    sortBy { it.priority }
                }
                .map { it.invoker }
                .toTypedArray()

        actionFlow.creator = ActionInvokerCreator {
            createActionInvoker(
                channels,
                actionRouter.level + 1,
                actionMatchers.subList(1, actionMatchers.size),
                controllerClass,
                actionMethod,
                instanceGetter,
                checkPf(ProcessFlowInfo<CTX>::beforeProcesses),
                checkPf(ProcessFlowInfo<CTX>::afterProcesses),
                checkPf(ProcessFlowInfo<CTX>::catchProcesses)
            ).apply {
                actionMatchers.first().let { first ->
                    if (first is StaticActionMatcher)
                        actionRouter.staticActions.getOrPut(paths[0]) { ArrayList() }.add(this)
                    else
                        let {
                            actionRouter.dynamicActions.firstOrNull { it.first == first }
                                ?: let {
                                    first to ArrayList<ActionInvoker<CTX>>()
                                }.apply { actionRouter.dynamicActions.add(this) }
                        }.second.add(this)
                }

            }
        }

        return actionFlow
    }

    abstract fun getSubStaticRouter(router: ROT, subPath: String): ROT
    abstract fun getSubDynamicRouter(router: ROT, matcher: RouterMatcher<CTX>): ROT
    abstract fun controllerChannel(annotation: Annotation?, controllerClass: Class<*>): List<String>
    abstract fun actionInfo(controllerChannel: List<String>, actionMethod: Method): Pair<String, List<String>>?

    abstract fun createActionInvoker(
        channels: List<String>,
        level: Int,
        matchers: List<RouterMatcher<CTX>>,
        actionClass: Class<*>,
        actionMethod: Method,
        instanceGetter: ControllerInstanceGetter,
        beforeProcesses: Array<ProcessInvoker<CTX>>,
        afterProcesses: Array<ProcessInvoker<CTX>>,
        catchProcesses: Array<ProcessInvoker<CTX>>,
    ): DssActionInvoker<CTX>

    abstract fun createMethodInvoker(
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<CTX>?

    abstract fun createCatchMethodInvoker(
        throwableType: Class<out Throwable>,
        controllerClass: Class<*>,
        targetMethod: Method,
        instanceGetter: ControllerInstanceGetter
    ): ProcessInvoker<CTX>?

}
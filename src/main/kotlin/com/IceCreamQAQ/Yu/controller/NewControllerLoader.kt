package com.IceCreamQAQ.Yu.controller

import com.IceCreamQAQ.Yu.annotation.*
import com.IceCreamQAQ.Yu.controller.router.*
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Named


abstract class NewControllerLoader : Loader {

    private val logger = LoggerFactory.getLogger(NewControllerLoader::class.java)

    @Inject
    private lateinit var context: YuContext

    open val separationCharacter: Array<String> = arrayOf("/")

    protected abstract fun createMethodInvoker(instance: Any, method: Method): NewMethodInvoker
    protected abstract fun createCatchMethodInvoker(instance: Any, method: Method, errorType: Class<out Throwable>): CatchInvoker
    protected abstract fun createActionInvoker(level: Int, actionMethod: Method, instance: Any): NewActionInvoker

    open class RootRouter {
        val router: NewRouter = NewRouterImpl(1)
        var globalBefores: MutableList<NewMethodInvoker> = ArrayList()
        var globalAfters: MutableList<NewMethodInvoker> = ArrayList()
        var globalCatchs: MutableList<CatchInvoker> = ArrayList()

        val globalBeforeList: MutableList<DoMethod> = ArrayList()
        val globalAfterList: MutableList<DoMethod> = ArrayList()
        val globalCatchList: MutableList<DoCatch> = ArrayList()
    }

    override fun load(items: Map<String, LoadItem>) {
        val rootRouters = HashMap<String, RootRouter>()
        for (item in items.values) {
            val clazz = item.type
            val name = clazz.getAnnotation(Named::class.java)?.value
                    ?: item.loadBy::class.java.interfaces[0].getAnnotation(Named::class.java)?.value ?: continue
            val rootRouter = rootRouters[name] ?: {
                val r = RootRouter()
                rootRouters[name] = r
                r
            }()

            controllerToRouter(context[clazz] ?: continue, rootRouter)
        }

        for (rrd in rootRouters.values) {
            val beforeList = rrd.globalBeforeList
            val afterList = rrd.globalAfterList
            val cacheList = rrd.globalCatchList

            for (i in 0 until beforeList.size) {
                for (j in 0 until beforeList.size - 1 - i) {
                    val c = beforeList[j]
                    val n = beforeList[j + 1]
                    if ((c.annotation as Before).weight > (n.annotation as Before).weight) {
                        beforeList[j] = n
                        beforeList[j + 1] = c
                    }
                }
            }

            for (i in 0 until afterList.size) {
                for (j in 0 until afterList.size - 1 - i) {
                    val c = afterList[j]
                    val n = afterList[j + 1]
                    if ((c.annotation as After).weight > (n.annotation as After).weight) {
                        afterList[j] = n
                        afterList[j + 1] = c
                    }
                }
            }

            for (i in 0 until cacheList.size) {
                for (j in 0 until cacheList.size - 1 - i) {
                    val c = cacheList[j]
                    val n = cacheList[j + 1]
                    if (c.catch.weight > n.catch.weight) {
                        cacheList[j] = n
                        cacheList[j + 1] = c
                    }
                }
            }

            val befores = rrd.globalBefores
            for (before in beforeList) befores.add(before.invoker)

            val afters = rrd.globalAfters
            for (after in afterList) afters.add(after.invoker)

            val catchs = rrd.globalCatchs
            for (catch in cacheList) catchs.add(catch.invoker)
        }

        for ((k, v) in rootRouters) {
            context.putBean(NewRouter::class.java, k, v.router)
        }
    }


    fun getMethods(methods: MutableList<Method>, clazz: Class<*>) {
        methods.addAll(clazz.methods)
        getMethods(methods, clazz.superclass ?: return)
    }

    data class DoMethod(val annotation: Annotation, val invoker: NewMethodInvoker)
    data class DoCatch(val catch: Catch, val invoker: CatchInvoker)
    data class ActionMap(val action: Action, val method: Method, val weight: Int = action.loadWeight)

    val p = Pattern.compile("\\{(.*?)}")

//    open


    open fun controllerToRouter(instance: Any, rootRouterData: RootRouter) {
        val rootRouter = rootRouterData.router as NewRouterImpl
        val globalBeforeList = rootRouterData.globalBeforeList
        val globalAfterList = rootRouterData.globalAfterList
        val globalCatchList = rootRouterData.globalCatchList

        val controllerClass = instance::class.java

        val controllerRouter = getRouterByPathString(rootRouter, controllerClass.getAnnotation(Path::class.java)?.value?.split(*separationCharacter), 0).router

        val allMethods = ArrayList<Method>()
        getMethods(allMethods, controllerClass)
        val with = controllerClass.getAnnotation(With::class.java)?.value
        if (with != null)
            for (kClass in with) {
                getMethods(allMethods, kClass.java)
            }

        val methods = controllerClass.methods
        val befores = ArrayList<DoMethod>()
        val afters = ArrayList<DoMethod>()
        val catchs = ArrayList<DoCatch>()

        for (method in allMethods) {
            val before = method.getAnnotation(Before::class.java)
            if (before != null) {
                val beforeInvoker = createMethodInvoker(instance, method)
                val dm = DoMethod(before, beforeInvoker)
                if (method.getAnnotation(Global::class.java) != null) globalBeforeList.add(dm)
                else befores.add(dm)
            }
            val after = method.getAnnotation(After::class.java)
            if (after != null) {
                val afterInvoker = createMethodInvoker(instance, method)
                val dm = DoMethod(after, afterInvoker)
                if (method.getAnnotation(Global::class.java) != null) globalAfterList.add(dm)
                else afters.add(dm)
            }
            val catch = method.getAnnotation(Catch::class.java)
            if (catch != null) {
                val catchInvoker = createCatchMethodInvoker(instance, method, catch.error.java)
                val dm = DoCatch(catch, catchInvoker)
                if (method.getAnnotation(Global::class.java) != null) globalCatchList.add(dm)
                else catchs.add(dm)
            }
        }

        for (i in 0 until befores.size) {
            for (j in 0 until befores.size - 1 - i) {
                val c = befores[j]
                val n = befores[j + 1]
                if ((c.annotation as Before).weight > (n.annotation as Before).weight) {
                    befores[j] = n
                    befores[j + 1] = c
                }
            }
        }

        for (i in 0 until afters.size) {
            for (j in 0 until afters.size - 1 - i) {
                val c = afters[j]
                val n = afters[j + 1]
                if ((c.annotation as After).weight > (n.annotation as After).weight) {
                    afters[j] = n
                    afters[j + 1] = c
                }
            }
        }

        for (i in 0 until catchs.size) {
            for (j in 0 until catchs.size - 1 - i) {
                val c = catchs[j]
                val n = catchs[j + 1]
                if (c.catch.weight > n.catch.weight) {
                    catchs[j] = n
                    catchs[j + 1] = c
                }
            }
        }

        val actionMap = ArrayList<ActionMap>()
        for (method in methods) actionMap.add(ActionMap(method.getAnnotation(Action::class.java) ?: continue, method))

        for (i in 0 until actionMap.size) {
            for (j in 0 until actionMap.size - 1 - i) {
                val c = actionMap[j]
                val n = actionMap[j + 1]
                if (c.weight > n.weight) {
                    actionMap[j] = n
                    actionMap[j + 1] = c
                }
            }
        }


//        val before = befores.toTypedArray()
        for (am in actionMap) {
            val method = am.method
            val action = am.action
            val actionMethodName = method.name

            val path = action.value
//                val aa = getActionRouter(path, controllerRouter, rootRouter)
//                val actionRootRouter = aa.router
//                val actionPath = aa.path

//                val methodInvoker = createMethodInvoker(instance, method)
//                actionInvoker.invoker = methodInvoker

            val abs = ArrayList<NewMethodInvoker>()
            w@ for ((before, invoker) in befores) {
                before as Before
                if (before.except.size != 1 || before.except[0] != "") for (s in before.except) {
                    if (s == actionMethodName) continue@w
                }
                if (before.only.size != 1 || before.only[0] != "") for (s in before.only) {
                    if (s != actionMethodName) continue@w
                }
                abs.add(invoker)
            }
            val aas = ArrayList<NewMethodInvoker>()
            w@ for ((after, invoker) in afters) {
                after as After
                if (after.except.size != 1 || after.except[0] != "") for (s in after.except) {
                    if (s == actionMethodName) continue@w
                }
                if (after.only.size != 1 || after.only[0] != "") for (s in after.only) {
                    if (s != actionMethodName) continue@w
                }
                aas.add(invoker)
            }
            val acs = ArrayList<CatchInvoker>()
            w@ for ((catch, invoker) in catchs) {
                if (catch.except.size != 1 || catch.except[0] != "") for (s in catch.except) {
                    if (s == actionMethodName) continue@w
                }
                if (catch.only.size != 1 || catch.only[0] != "") for (s in catch.only) {
                    if (s != actionMethodName) continue@w
                }
                acs.add(invoker)
            }


            val actionInvoker = getActionInvoker(path, controllerRouter, rootRouter, method, instance)

            actionInvoker.globalBefores = rootRouterData.globalBefores
            actionInvoker.globalAfters = rootRouterData.globalAfters
            actionInvoker.globalCatchs = rootRouterData.globalCatchs

            actionInvoker.befores = abs.toTypedArray()
            actionInvoker.afters = aas.toTypedArray()
            actionInvoker.catchs = acs.toTypedArray()

            val synonym = method.getAnnotation(Synonym::class.java) ?: continue
            for (s in synonym.value) {
                val sai = getActionInvoker(s, controllerRouter, rootRouter, method, instance)

                sai.globalBefores = rootRouterData.globalBefores
                sai.globalAfters = rootRouterData.globalAfters
                sai.globalCatchs = rootRouterData.globalCatchs

                sai.befores = abs.toTypedArray()
                sai.afters = aas.toTypedArray()
                sai.catchs = acs.toTypedArray()
            }

        }

    }

    fun getMatchItem(pathString: String, nextRouter: NewRouterImpl): MatchItem? {
        return if (pathString.startsWith("\\") && pathString.endsWith("\\"))
            MatchItem(
                    false,
                    pathString.substring(1).substring(0, pathString.length - 2),
                    null,
                    nextRouter
            )
        else {
//            val pvs = ArrayList<String>()
//            val m = p.matcher(path)
//            while (m.find()) pvs.add(m.group(1))
//            if (pvs.size == 0) null
//            else {
//                val matchNames = ArrayList<String>()
//                for (pv in pvs) {
//                    if (pv.contains(":")) {
//                        val s = pv.split(":")
//                        path = path.replace("{$pv}", "(${s[1]})")
//                        matchNames.add(s[0])
//                    } else {
//                        path = path.replace("{$pv}", "(.*)")
//                        matchNames.add(pv)
//                    }
//                }
//                MatchItem(
//                        true,
//                        "^$path",
//                        matchNames.toTypedArray(),
//                        nextRouter
//                )
//            }
            if (!pathString.contains("{") || !pathString.contains("}")) null
            else {
                val pvs = ArrayList<String>()

                val newPath = StringBuilder("^")

                var startSearch = false

                var name = StringBuilder()
                var regex: StringBuilder? = null

                var lessD = 0
                var haveM = false

                for (c in pathString) {
                    if (c == '{') if (!startSearch) {
                        startSearch = true
                        continue
                    } else lessD += 1
                    if (startSearch) {
                        if (c == '}') {
                            if (lessD != 0) lessD -= 1
                            else {
                                pvs.add(name.toString())

                                if (haveM) regex!!.append(")")
                                newPath.append(regex?.toString() ?: "(.*)")

                                name = StringBuilder()
                                regex = null

                                lessD = 0
                                haveM = false
                                startSearch = false
                                continue
                            }
                        }
                        if (haveM) {
                            regex!!.append(c)
                        } else {
                            if (c == ':') {
                                haveM = true
                                regex = StringBuilder("(")
                            } else name.append(c)
                        }
                    } else newPath.append(c)
                }

                MatchItem(
                        true,
                        newPath.toString(),
                        pvs.toTypedArray(),
                        nextRouter
                )
            }
        }
    }

    data class ActionRouterAndPath(val router: NewRouterImpl, val path: String)

    fun getActionInvoker(path: String, controllerRouter: NewRouterImpl, rootRouter: NewRouterImpl, method: Method, instance: Any): NewActionInvoker {
        val aa = getActionRouter(path, controllerRouter, rootRouter)
        val actionRootRouter = aa.router
        val actionPath = aa.path

        val actionInvoker = createActionInvoker(actionRootRouter.level + 1, method, instance)

        val mi = getMatchItem(actionPath, actionInvoker)

        if (mi == null) {
            val router = actionRootRouter.noMatch[actionPath]
            if (router != null) {
                actionInvoker.needMath.addAll(router.needMath)
                actionInvoker.noMatch.putAll(router.noMatch)
            }
            actionRootRouter.noMatch[actionPath] = actionInvoker
        } else actionRootRouter.needMath.add(mi)

        return actionInvoker
    }

    fun getActionRouter(pathString: String, controllerRouter: NewRouterImpl, rootRouter: NewRouterImpl): ActionRouterAndPath {
        var path = pathString
        val router = if (path[0] == '/') {
            path = path.substring(1)
            rootRouter
        } else {
            controllerRouter
        }
        val a = path.split(*separationCharacter)
        return if (a.size == 1) ActionRouterAndPath(router, path)
        else getRouterByPathString(router, a, 1)
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

    fun getRouterByPathString(router: NewRouterImpl, paths: List<String>?, lessLevel: Int): ActionRouterAndPath {
        if (paths == null) return ActionRouterAndPath(router, "")
//        val paths = pathString.split(*separationCharacter)
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
    override fun createMethodInvoker(instance: Any, method: Method): NewMethodInvoker = NewReflectMethodInvoker(method, instance)
    override fun createCatchMethodInvoker(instance: Any, method: Method, errorType: Class<out Throwable>) = ReflectCatchInvoker(errorType, method, instance)

    override fun createActionInvoker(level: Int, actionMethod: Method, instance: Any): NewActionInvoker = NewActionInvoker(level, actionMethod, instance)
}
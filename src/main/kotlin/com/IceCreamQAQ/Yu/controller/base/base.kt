package com.IceCreamQAQ.Yu.controller.base

import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.fullName
import com.IceCreamQAQ.Yu.util.bubbleSort
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

abstract class BaseActionInvoker(
    final override val method: Method,
    override val instance: Any,
    override val width: Int
) : ActionInvoker {
    override val controllerName: String = method.declaringClass.name
    override val actionName: String = method.name
    override val actionFullName: String = method.fullName

    override lateinit var befores: Array<MethodInvoker>
    override lateinit var afters: Array<MethodInvoker>
    override lateinit var catchs: Array<CatchInvoker>

    override lateinit var invoker: MethodInvoker

    override var interceptorInfo: InterceptorInfo? = null
}

abstract class BaseRouter : Router {
    abstract val actionInvokers: MutableList<ActionInvoker>

    abstract fun initChildren(rootRouter: RouterInfo)
    abstract suspend fun invokerChildren(context: ActionContext, paras: ParasMap): Boolean
    abstract fun getLocalParas(context: ActionContext, superParas: ParasMap): ParasMap

    override fun init(rootRouter: RouterInfo) {
        initChildren(rootRouter)
        for (action in actionInvokers) {
            action.init(rootRouter)
        }
        actionInvokers.bubbleSort(ActionInvoker::width)
    }

    override suspend operator fun invoke(context: ActionContext, superParas: ParasMap): Boolean {
        val localParas = getLocalParas(context, superParas)
        if (invokerChildren(context, localParas)) return true
        for (action in actionInvokers) {
            if (action.invoke(context, localParas)) return true
        }
        return false
    }
}

abstract class BaseActionContext : ActionContext {

    open val saves = HashMap<String, Any>()
    override fun get(name: String): Any? = saves[name]
    override fun set(name: String, obj: Any) {
        saves[name] = obj
    }

}

abstract class BaseActionInfo(
    override val name: String,
    override val fullName: String,
//    override val path: String,
//    override val fullPath: String,
//    override val enableMethod: Array<String>,
    override val invoker: ActionInvoker
) : ActionInfo

abstract class BaseReflectMethodInvoker(val method: Method, val instance: Any) : MethodInvoker {
    override val methodName: String
        get() = method.name
    override val fullName: String = method.fullName

    abstract fun getInvokeParas(context: ActionContext): Array<Any?>
    abstract fun invokeSuccess(context: ActionContext, result: Any?)

    open val isNoPara get() = method.parameters.isEmpty()
    open val kFun get() = method.kotlinFunction
    open val isSuspendFun get() = kFun?.isSuspend ?: false

    override suspend fun invoke(context: ActionContext) =
        getInvokeParas(context).let {
            when {
                isSuspendFun -> kFun!!.callSuspend(instance, *it)
                isNoPara -> method.invoke(instance)
                else -> method.invoke(instance, *it)
            }
        }.let { invokeSuccess(context, it) }

}

abstract class BaseCatchInvoker(open val type: Class<out Throwable>) : CatchInvoker {

    abstract val methodInvoker: MethodInvoker

    override val methodName: String
        get() = methodInvoker.methodName
    override val fullName: String
        get() = methodInvoker.fullName

    override fun init() {
        methodInvoker.init()
    }

    override suspend fun invoke(context: ActionContext, error: Throwable): Any? {
        if (!type.isAssignableFrom(error::class.java)) return null
        return methodInvoker.invoke(context)
    }

}


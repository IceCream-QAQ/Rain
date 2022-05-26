package com.IceCreamQAQ.Yu.controller.default

import com.IceCreamQAQ.Yu.controller.*
import com.IceCreamQAQ.Yu.controller.base.*
import java.lang.reflect.Method
import java.util.regex.Pattern
import javax.inject.Named

open class DefaultRootRouter : RouterInfo {
    override val router = DefaultRouter(0)
    override val interceptorInfo = InterceptorInfo(arrayListOf(), arrayListOf(), arrayListOf())
    override val allAction: MutableList<ActionInfo> = arrayListOf()
}

open class DefaultMatchRouter(
    level: Int,
    open val needSave: Boolean,
    open val routerString: String,
    regex: String,
    open val matchNames: Array<String>?,
) : DefaultRouter(level) {

    open val p: Pattern = Pattern.compile(regex)

    fun match(path: String, context: ActionContext): Boolean =
        p.matcher(path).let { m ->
            if (m.find()) {
                if (needSave) {
                    for ((i, name) in matchNames!!.withIndex()) {
                        context[name] = m.group(i + 1)
                    }
                }
                true
            } else false
        }

}

open class DefaultRouter(open val level: Int) : BaseRouter() {

    open val staticRouterMap = HashMap<String, Router>()
    open val needMatchRouterMap = HashMap<String, DefaultMatchRouter>()
    override var actionInvokers: MutableList<ActionInvoker> = arrayListOf()

    override fun initChildren(rootRouter: RouterInfo) {
        staticRouterMap.values.forEach { it.init(rootRouter) }
        needMatchRouterMap.values.forEach { it.init(rootRouter) }
    }

    override fun getLocalParas(context: ActionContext, superParas: ParasMap) = superParas

    override suspend fun invokerChildren(context: ActionContext, paras: ParasMap): Boolean {
        context as DefaultActionContext
        if (level >= context.path.size) return false
        val path = context.path[level]
        if (staticRouterMap[path]?.let { invoke(context, paras) } == true) return true
        for (matchRouter in needMatchRouterMap.values) {
            if (matchRouter.match(path, context) && matchRouter.invoke(context,paras)) return true
        }
        return false
    }


}

open class DefaultActionContext(open val path: Array<String>) : BaseActionContext() {

    var result: Any? = null

    override suspend fun onError(e: Throwable): Throwable = e

    override suspend fun onSuccess(result: Any?): Any? {
        this.result = result
        return null
    }

}

open class DefaultCatchInvoker(type: Class<out Throwable>, override val methodInvoker: MethodInvoker) :
    BaseCatchInvoker(type)

open class DefaultActionInvoker(
    method: Method,
    instance: Any,
    width: Int
) : BaseActionInvoker(method, instance, width) {
    override fun createActionMethodInvoker(method: Method, instance: Any) =
        DefaultReflectMethodInvoker(method, instance)
}

open class ParaInfo(
    val clazz: Class<*>,
    val type: Int,
    val name: String
)

open class DefaultReflectMethodInvoker(method: Method, instance: Any) : BaseReflectMethodInvoker(method, instance) {

    open var mps: Array<ParaInfo>? = null

    override fun init() {
        if (!isNoPara) {
            val paraNum = method.parameters.size - if (isSuspendFun) 1 else 0
            if (paraNum == 0) return
            val mps = arrayOfNulls<ParaInfo>(paraNum)
            for (i in 0 until paraNum) {
                val para = method.parameters[i]
                val name = para.getAnnotation(Named::class.java)?.value
                    ?: error("创建 MethodInvoker: $fullName 失败！方法参数: $i 无法找到 Named 注解！")
                mps[i] = ParaInfo(para.type, 0, name)
            }
            this.mps = mps as Array<ParaInfo>
        }
    }

    override fun getInvokeParas(context: ActionContext): Array<Any?> {
        return mps?.map { context[it.name] }?.toTypedArray() ?: emptyArray()
    }

    override fun invokeSuccess(context: ActionContext, result: Any?) {
        (context as DefaultActionContext).result = result
    }

}

open class DefaultRouterPath(open val path: String, open val name: String?, open val saves: Array<String>?) {
    override fun toString(): String {
        return "DefaultRouterPath(path='$path', name=$name, saves=${saves?.contentToString()})"
    }
}
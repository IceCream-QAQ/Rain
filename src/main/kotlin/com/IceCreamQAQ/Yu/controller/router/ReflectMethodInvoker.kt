package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.controller.ActionContext
import java.lang.reflect.Method
import javax.inject.Named

@Deprecated("已经弃用")
class ReflectMethodInvoker(val method: Method, val instance: Any) : MethodInvoker {

    var returnFlag: Boolean = false
    var mps: Array<MethodPara?>? = null

    init {
        returnFlag = (method.returnType?.name ?: "void") != "void"

        val paras = method.parameters!!
        val mps = arrayOfNulls<MethodPara>(paras.size)

        for (i in paras.indices) {
            val para = paras[i]!!
//            val pathVar = para.getAnnotation(PathVar::class.java)
//            if (pathVar != null) {
//                mps[i] = MethodPara(para.type, 1, pathVar)
//                continue
//            }
            val name = para.getAnnotation(Named::class.java)!!
            mps[i] = MethodPara(para.type, 0, name.value)
        }

        this.mps = mps
    }

    override fun invoke(context: ActionContext): Any? {
        val mps = mps!!
        val paras = arrayOfNulls<Any>(mps.size)

        for (i in mps.indices) {
            val mp = mps[i] ?: continue
            paras[i] = when (mp.type) {
                0 -> context[mp.data as String]
                else -> null
            }
        }

        val re = if (mps.isEmpty()) method.invoke(instance)

        else {
            method.invoke(instance, *paras)
        }
        if (returnFlag) return re
        return null
    }

    data class MethodPara(
            val clazz: Class<*>,
            val type: Int,
            val data: Any
    )
}
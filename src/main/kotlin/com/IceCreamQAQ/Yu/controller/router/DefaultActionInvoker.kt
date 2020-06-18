package com.IceCreamQAQ.Yu.controller.router

import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.entity.DoNone
import com.IceCreamQAQ.Yu.entity.Result
import java.lang.Exception

@Deprecated("已经弃用")
open class DefaultActionInvoker(level: Int) : DefaultRouter(level) {

    lateinit var befores: Array<MethodInvoker>
    lateinit var invoker: MethodInvoker

    override fun invoke(path: String, context: ActionContext): Boolean {
        if (super.invoke(path, context)) return true
        try {
            for (before in befores) {
                val o = before.invoke(context)
                if (o != null) context[toLowerCaseFirstOne(o::class.java.simpleName)] = o
            }
            val result = invoker.invoke(context) ?: return true
            if (result is Result) context.result = result
            else context.result = context.buildResult(result)
        } catch (e: DoNone) {
        } catch (e: Result) {
            context.result = e
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    private fun toLowerCaseFirstOne(s: String): String {
        return if (Character.isLowerCase(s[0])) s
        else (StringBuilder()).append(Character.toLowerCase(s[0])).append(s.substring(1)).toString();
    }
}
package com.IceCreamQAQ.Yu.validation.global

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.hook.HookContext
import com.IceCreamQAQ.Yu.hook.HookInfo
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.IceCreamQAQ.Yu.nameWithParamsFullClass
import com.IceCreamQAQ.Yu.validation.ValidateBy
import com.IceCreamQAQ.Yu.validation.ValidateFailException
import com.IceCreamQAQ.Yu.validation.ValidateResult

class ValidationHook : HookRunnable {
    val globalValidatorFactory = GlobalValidatorFactory()

    override fun init(info: HookInfo) {
        val method = info.method
        info.saveInfo["Rain.Validator"] =
            arrayListOf<List<(Any?) -> ValidateResult?>?>(null).apply {
                addAll(
                    method.parameters.map {
                        val invoker: ArrayList<(Any?) -> ValidateResult?> = ArrayList()

                        it.annotations.forEach { an ->
                            an::class.java.interfaces[0].annotation<ValidateBy> {
                                val validator = globalValidatorFactory[value]
                                invoker.add { param -> validator.validate(an, param) }
                            }
                        }

                        if (invoker.isEmpty()) null else invoker
                    }
                )
            }

    }

    override fun preRun(context: HookContext): Boolean {
        let { context.info.saveInfo["Rain.Validator"] as List<List<(Any?) -> ValidateResult?>?> }
            .forEachIndexed { i, list ->
                val param = context.params[i]
                list?.forEach {
                    it(param)?.let {
                        throw ValidateFailException(context.info.method.nameWithParamsFullClass, "第 $i 参数", it)
                    }
                }
            }

        return false
    }
}
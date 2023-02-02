package com.IceCreamQAQ.Yu.validation.global

import com.IceCreamQAQ.Yu.validation.Validator
import kotlin.reflect.KClass

class GlobalValidatorFactory {

    private val validatorMap = hashMapOf<KClass<out Validator>, Validator>()
    private val classValidatorMap = hashMapOf<Class<*>, Validator>()

    operator fun get(clazz: KClass<out Validator>) =
        validatorMap.getOrPut(clazz) {
            kotlin.runCatching { clazz.java.newInstance() }.getOrNull()
                ?: error("参数校验器：${clazz.java.name} 初始化失败。")
        }

    operator fun get(clazz: Class<*>) =
        classValidatorMap.getOrPut(clazz) {
            TODO("类验证器部分暂未实现。")
//            context.newBean(creator.spawnClassValidator(clazz)) ?: error("对象属性校验器：${clazz.name} 初始化失败。")
        }


}
package com.IceCreamQAQ.Yu.validation

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.YuContext.Companion.get
import com.IceCreamQAQ.YuWeb.validation.ClassValidatorCreator
import javax.inject.Inject
import kotlin.reflect.KClass

class ValidatorFactory {

    @Inject
    private lateinit var context: YuContext

    @Inject
    private lateinit var creator: ClassValidatorCreator

    private val validatorMap = hashMapOf<KClass<out Validator>, Validator>()
    private val classValidatorMap = hashMapOf<Class<*>, Validator>()

    operator fun get(clazz: KClass<out Validator>) = validatorMap.getOrPut(clazz) {
        context[clazz.java] ?: error("参数校验器：${clazz.java.name} 初始化失败。")
    }

    operator fun get(clazz: Class<*>) = classValidatorMap.getOrPut(clazz) {
        context.newBean(creator.spawnClassValidator(clazz)) ?: error("对象属性校验器：${clazz.name} 初始化失败。")
    }


}
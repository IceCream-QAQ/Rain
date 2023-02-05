package com.IceCreamQAQ.YuWeb.validation

import com.IceCreamQAQ.Yu.validation.*

abstract class ClassValidatorBase @JvmOverloads constructor(factory: ValidatorFactory, val clazz: Class<*>, paras: Array<String>? = null) :
    Validator {

    //    open val clazz: Class<*> = Class.forName(clazzName)
    private val reflectValidateData: Array<ParaValidateData>? = paras?.let {
        val rvd = arrayListOf<ParaValidateData>()
        for (s in it) {
            val field = clazz.getDeclaredField(s)
            for (annotation in field.annotations) {
                annotation::class.java.interfaces[0].getAnnotation(ValidateBy::class.java)?.let { vb ->
                    rvd.add(ParaValidateData(field, annotation, factory[vb.value]))
                }
            }
        }
        rvd.toTypedArray()
    }
    open val className: String = clazz.name


    fun reflectValidate(bean: Any): ValidateResult? {
        if (!clazz.isInstance(bean))
            return ValidateResult("在进行对象属性值比对时发生错误，对象不是验证器所允许的对象类型！验证器：${this::class.java.name}，被验证对象：${bean::class.java.name}，允许的类型：${clazz.name}。", this)
        reflectValidateData?.let {
            for (data in it) {
                data.field.isAccessible = true
                data.validator.validate(data.annotation, data.field[bean])?.let { result ->
                    return result
                }
            }
        }
        return null
    }

    fun buildResult(paraName: String, result: ValidateResult): ValidateResult {
        result.fullMessage = "$className.$paraName 验证失败！${result.fullMessage}。"
        return result
    }

}
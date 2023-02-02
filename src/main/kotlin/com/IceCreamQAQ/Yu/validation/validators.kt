package com.IceCreamQAQ.Yu.validation

import com.IceCreamQAQ.Yu.annotation.HookBy
import kotlin.reflect.KClass

annotation class Valid

@HookBy(value = "com.IceCreamQAQ.Yu.validation.global.ValidationHook")
annotation class ValidHook
annotation class NoValidHook

annotation class ValidateBy(
    val value: KClass<out Validator>
)


@ValidateBy(NullValidator::class)
annotation class Null(
    val message: String = "",
)

class NullValidator : Validator {
    override fun validate(annotation: Annotation, bean: Any?): ValidateResult? {
        return (annotation as? Null)?.run {
            if (bean != null) ValidateResult(message, this@NullValidator, annotation = this)
            else return null
        } ?: ValidateResult("在进行对象属性值比对时发生错误，对象不是验证器所允许的注解类型！验证器：${this::class.java.name}，提供注解：${annotation::class.java.interfaces[0].name}，允许的类型：${Null::class.java.name}。", this, annotation = annotation)
    }
}

@ValidateBy(NotNullValidator::class)
annotation class NotNull(
    val message: String = "",
)

class NotNullValidator : Validator {
    override fun validate(annotation: Annotation, bean: Any?): ValidateResult? {
        return (annotation as? NotNull)?.run {
            if (bean == null) ValidateResult(message, this@NotNullValidator, annotation = this)
            else return null
        } ?: ValidateResult("在进行对象属性值比对时发生错误，对象不是验证器所允许的注解类型！验证器：${this::class.java.name}，提供注解：${annotation::class.java.interfaces[0].name}，允许的类型：${NotNull::class.java.name}。", this, annotation = annotation)
    }
}

@ValidateBy(MinValidator::class)
@Target(AnnotationTarget.FIELD,AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class Min(
    val value: Long,
    val canEqual: Boolean = false,
    val message: String = "所提供数值过小！",
)

class MinValidator : ValidatorBase<Min>() {

    override fun doNotNullValidate(annotation: Min, bean: Any): ValidateResult? {
        val number = when (bean) {
            is Short -> bean.toLong()
            is Int -> bean.toLong()
            is Long -> bean.toLong()
            is Float -> bean.toLong()
            is Double -> bean.toLong()
            else -> return buildResult("对象并非数值！", annotation)
        }

        return if (annotation.canEqual) if (annotation.value <= number) null else annotation.buildResult()
        else if (annotation.value < number) null else annotation.buildResult()
    }

    override fun messageFun(annotation: Min) = annotation.message

}

fun Validator.buildResult(
    message: String,
    annotation: Annotation? = null,
    fullMessage: String = message,
) = ValidateResult(message, this, fullMessage, annotation)

@ValidateBy(MaxValidator::class)
annotation class Max(
    val value: Long,
    val canEqual: Boolean = false,
    val message: String = "所提供数值过大！",
)

class MaxValidator : ValidatorBase<Max>() {

    override fun doNotNullValidate(annotation: Max, bean: Any): ValidateResult? {
        val number = when (bean) {
            is Short -> bean.toLong()
            is Int -> bean.toLong()
            is Long -> bean.toLong()
            is Float -> bean.toLong()
            is Double -> bean.toLong()
            else -> return buildResult("对象并非数值！", annotation)
        }

        return if (annotation.canEqual) if (annotation.value >= number) null else annotation.buildResult()
        else if (annotation.value > number) null else annotation.buildResult()
    }

    override fun messageFun(annotation: Max) = annotation.message

}
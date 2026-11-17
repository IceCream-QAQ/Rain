package rain.validator.validators

import rain.validator.ValidateBy
import rain.validator.ValidateResult
import rain.validator.ValidatorBase
import rain.validator.buildResult


@ValidateBy(MinValidator::class)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
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
            is Long -> bean
            is Float -> bean.toLong()
            is Double -> bean.toLong()
            else -> return buildResult("对象并非数值！", annotation)
        }

        return if (annotation.canEqual) {
            if (annotation.value <= number) null else annotation.buildResult()
        } else {
            if (annotation.value < number) null else annotation.buildResult()
        }
    }

    override fun messageFun(annotation: Min) = annotation.message
}
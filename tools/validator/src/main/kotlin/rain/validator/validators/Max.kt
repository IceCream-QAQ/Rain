package rain.validator.validators

import rain.validator.ValidateBy
import rain.validator.ValidateResult
import rain.validator.ValidatorBase
import rain.validator.buildResult

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
            is Long -> bean
            is Float -> bean.toLong()
            is Double -> bean.toLong()
            else -> return buildResult("对象并非数值！", annotation)
        }

        return if (annotation.canEqual) {
            if (annotation.value >= number) null else annotation.buildResult()
        } else {
            if (annotation.value > number) null else annotation.buildResult()
        }
    }

    override fun messageFun(annotation: Max) = annotation.message
}
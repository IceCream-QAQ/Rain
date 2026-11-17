package rain.validator.validators

import rain.validator.ValidateBy
import rain.validator.ValidateResult
import rain.validator.Validator

@ValidateBy(NullValidator::class)
annotation class Null(
    val message: String = "",
)

class NullValidator : Validator {
    override fun validate(annotation: Annotation, bean: Any?): ValidateResult? {
        val nullAnnotation = annotation as? Null
            ?: return ValidateResult(
                "在进行对象属性值比对时发生错误，对象不是验证器所允许的注解类型！" +
                    "验证器：${this::class.java.name}，提供的类型：${annotation.annotationClass.java.name}，" +
                    "允许的类型：${Null::class.java.name}。",
                this,
                annotation = annotation,
            )

        return if (bean != null) {
            ValidateResult(nullAnnotation.message, this, annotation = nullAnnotation)
        } else {
            null
        }
    }
}

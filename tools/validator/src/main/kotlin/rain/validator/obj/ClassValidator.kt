package rain.validator.obj

import rain.classloader.IRainClassLoader
import rain.validator.ValidateResult
import rain.validator.Validator
import rain.validator.ValidatorFactory
import java.lang.reflect.Modifier

class ClassValidator(
    val targetClass: Class<*>,
    val fields: List<FieldValidation>,
) : Validator {

    override fun validate(annotation: Annotation, bean: Any?): ValidateResult? {
        if (bean == null) {
            return ValidateResult(
                "对象值为空！",
                this,
                annotation = annotation,
            )
        }
        if (!targetClass.isInstance(bean)) {
            return ValidateResult(
                "在进行对象属性值比对时发生错误，对象不是验证器所允许的对象类型！" +
                        "验证器：${this::class.java.name}，被验证对象：${bean::class.java.name}，" +
                        "允许的类型：${targetClass.name}。",
                this,
                annotation = annotation,
            )
        }

        fields.forEach { fieldValidation ->
            val value = fieldValidation.reader.read(bean)
            fieldValidation.validators.forEach { rule ->
                rule.validator.validate(rule.annotation, value)?.let { result ->
                    result.fullMessage =
                        "${targetClass.name}.${fieldValidation.field.name} 验证失败！${result.fullMessage}。"
                    return result
                }
            }
        }
        return null
    }

    companion object {
        fun create(
            targetClass: Class<*>,
            factory: ValidatorFactory,
            classloader: IRainClassLoader? = null,
        ): ClassValidator {
            val fieldValidations = targetClass.declaredFields
                .asSequence()
                .filterNot { Modifier.isStatic(it.modifiers) }
                .mapNotNull { field ->
                    val rules = field.annotations.mapNotNull { fieldAnnotation ->
                        factory[fieldAnnotation]?.let { validator ->
                            ValidationRule(fieldAnnotation, validator)
                        }
                    }
                    if (rules.isEmpty()) return@mapNotNull null
                    FieldValidation(field, FieldReader.fieldReaderOf(targetClass, field, classloader), rules)
                }
                .toList()

            return ClassValidator(targetClass, fieldValidations)
        }
    }
}

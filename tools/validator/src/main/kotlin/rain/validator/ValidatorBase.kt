package rain.validator

import java.lang.reflect.ParameterizedType

abstract class ValidatorBase<T : Annotation> : Validator {

    @Suppress("UNCHECKED_CAST")
    private val allowedAnnotationClass: Class<T> =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>

    private val invalidAnnotationMessage =
        "在进行对象属性值比对时发生错误，声明注解不是验证器所允许的注解类型！" +
            "验证器：${this::class.java.name}，允许的注解：${allowedAnnotationClass.name}，提供的类型：%s。"

    private val nullValueMessage =
        "在进行对象属性值比对时发生错误，对象值为空！" +
            "验证器：${this::class.java.name}，允许的注解：${allowedAnnotationClass.name}，提供的类型：%s。"

    fun T.buildResult(
        message: String = messageFun(this),
        fullMessage: String = message,
    ) = ValidateResult(message, this@ValidatorBase, fullMessage, this)

    abstract fun messageFun(annotation: T): String

    override fun validate(annotation: Annotation, bean: Any?): ValidateResult? {
        if (!allowedAnnotationClass.isInstance(annotation)) {
            return ValidateResult(
                invalidAnnotationMessage.format(annotation.annotationClass.java.name),
                this,
                annotation = annotation,
            )
        }
        return doValidate(annotation as T, bean)
    }

    open fun doValidate(annotation: T, bean: Any?): ValidateResult? {
        if (bean == null) {
            return ValidateResult(
                nullValueMessage.format(annotation.annotationClass.java.name),
                this,
                annotation = annotation,
            )
        }
        return doNotNullValidate(annotation, bean)
    }

    open fun doNotNullValidate(annotation: T, bean: Any): ValidateResult? = null
}

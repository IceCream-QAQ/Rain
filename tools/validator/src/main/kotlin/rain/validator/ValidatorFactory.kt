package rain.validator

import java.util.concurrent.ConcurrentHashMap
import rain.classloader.IRainClassLoader
import rain.validator.obj.ClassValidator
import kotlin.reflect.KClass

class ValidatorFactory(
    private val creator: (KClass<out Validator>) -> Validator = { clazz ->
        clazz.java.getDeclaredConstructor().apply { isAccessible = true }.newInstance()
    },
    private val classloader: IRainClassLoader? = null,
) {

    private val validatorMap = ConcurrentHashMap<KClass<out Validator>, Validator>()
    private val classValidatorMap = ConcurrentHashMap<Class<*>, Validator>()

    operator fun get(clazz: KClass<out Validator>): Validator =
        validatorMap.computeIfAbsent(clazz) { creator(it) }

    operator fun get(annotation: Annotation): Validator? =
        annotation.annotationClass.java
            .getAnnotation(ValidateBy::class.java)
            ?.value
            ?.let(::get)

    operator fun get(clazz: Class<*>): Validator =
        classValidatorMap.computeIfAbsent(clazz) { targetClass ->
            ClassValidator.create(targetClass, this,  classloader)
        }

    fun validate(annotation: Annotation, bean: Any?): ValidateResult? =
        get(annotation)?.validate(annotation, bean)

    fun register(clazz: KClass<out Validator>, validator: Validator): Validator {
        validatorMap[clazz] = validator
        return validator
    }

}

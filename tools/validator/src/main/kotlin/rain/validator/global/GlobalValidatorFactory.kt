package rain.validator.global

import rain.validator.Validator
import rain.validator.ValidatorFactory
import kotlin.reflect.KClass

/** Validator lookup used by the global Hook implementation. */
class GlobalValidatorFactory(
    private val factory: ValidatorFactory = ValidatorFactory(),
) {

    operator fun get(clazz: KClass<out Validator>): Validator = factory[clazz]

    operator fun get(clazz: Class<*>): Validator = factory[clazz]

    operator fun get(annotation: Annotation): Validator? = factory[annotation]

}

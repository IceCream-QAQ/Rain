package rain.validator

import rain.hook.HookBy
import kotlin.reflect.KClass

annotation class Valid

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@HookBy("rain.validator.global.ValidationHook")
annotation class ValidHook

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoValidHook

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidateBy(
    val value: KClass<out Validator>,
)

fun Validator.buildResult(
    message: String,
    annotation: Annotation? = null,
    fullMessage: String = message,
) = ValidateResult(message, this, fullMessage, annotation)



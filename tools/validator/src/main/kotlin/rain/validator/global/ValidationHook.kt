package rain.validator.global

import rain.function.nameWithParamsFullClass
import rain.hook.HookContext
import rain.hook.HookInfo
import rain.hook.HookRunnable
import rain.validator.ValidateFailException
import rain.validator.ValidateResult

private typealias ValueValidation = (Any?) -> ValidateResult?

/** Runs validators attached to method parameters before the target method. */
class ValidationHook : HookRunnable {

    companion object {
        private const val validationKey = "Rain.Validator"
    }

    private val globalValidatorFactory = GlobalValidatorFactory()

    override fun init(info: HookInfo) {
        val validations: List<List<ValueValidation>?> = info.method.parameters.map { parameter ->
            val checks = parameter.annotations.mapNotNull { annotation ->
                globalValidatorFactory[annotation]?.let { validator ->
                    val check: ValueValidation = { value -> validator.validate(annotation, value) }
                    check
                }
            }
            checks.takeIf { it.isNotEmpty() }
        }
        info.saveInfo[validationKey] = validations
    }

    @Suppress("UNCHECKED_CAST")
    override fun preRun(context: HookContext): Boolean {
        val validations = context.info.saveInfo[validationKey] as? List<List<ValueValidation>?>
            ?: return false

        validations.forEachIndexed { index, checks ->
            checks?.forEach { check ->
                check(context.params[index])?.let { result ->
                    throw ValidateFailException(
                        context.info.method.nameWithParamsFullClass,
                        "第 $index 参数",
                        result,
                    )
                }
            }
        }
        return false
    }
}

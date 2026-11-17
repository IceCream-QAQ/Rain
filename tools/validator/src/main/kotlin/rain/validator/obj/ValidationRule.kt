package rain.validator.obj

import rain.validator.Validator

data class ValidationRule(
    val annotation: Annotation,
    val validator: Validator,
)
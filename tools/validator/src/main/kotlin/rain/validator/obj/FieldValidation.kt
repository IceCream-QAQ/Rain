package rain.validator.obj

import java.lang.reflect.Field

data class FieldValidation(
    val field: Field,
    val reader: FieldReader,
    val validators: List<ValidationRule>,
)
package rain.validator

open class ValidateResult @JvmOverloads constructor(
    val message: String,
    val validator: Validator,
    var fullMessage: String = message,
    val annotation: Annotation? = null,
)

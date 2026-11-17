package rain.validator

interface Validator {

    fun validate(annotation: Annotation, bean: Any?): ValidateResult?

}

package com.IceCreamQAQ.Yu.validation

interface Validator {

    fun validate(annotation: Annotation, bean: Any?): ValidateResult?

}
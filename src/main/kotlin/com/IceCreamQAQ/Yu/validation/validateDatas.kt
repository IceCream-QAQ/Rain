package com.IceCreamQAQ.Yu.validation

import java.lang.reflect.Field

data class ValidateData(
        val annotation: Annotation,
        val validator: Validator,
)

data class ParaValidateData(
        val field: Field,
        val annotation: Annotation,
        val validator: Validator,
)
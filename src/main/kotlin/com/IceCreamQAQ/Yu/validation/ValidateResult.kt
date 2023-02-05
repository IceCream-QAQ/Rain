package com.IceCreamQAQ.Yu.validation

open class ValidateResult @JvmOverloads constructor(
    val message: String,
    val validator: Validator,
    var fullMessage: String = message,
    val annotation: Annotation? = null,
)
package com.IceCreamQAQ.Yu.error

import org.eclipse.jdt.core.compiler.CategorizedProblem

class BeanCreateError(message: String) : Exception(message)
class ControllerLoadErr(message: String) : Exception(message)

class InvokerClassCreateException(message: String, pb: Array<CategorizedProblem>, cause: Throwable? = null) : RuntimeException(message, cause)
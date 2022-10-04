package com.IceCreamQAQ.Yu.util.classMaker.ecj

import com.IceCreamQAQ.Yu.util.sout
import com.IceCreamQAQ.Yu.util.subStringByLast
import com.IceCreamQAQ.Yu.util.classMaker.*

open class MethodMaker @JvmOverloads constructor(
    open val name: String,
    open var access: Access = Access.PUBLIC,
    open var static: Boolean = false,
    open var final: Boolean = false,
    open var parameters: Array<MethodParameter>? = null,
    open var returnType: Class<*> = Unit::class.java
) : AnnotationAble {
    var body: String = ""
    override val annotations = arrayListOf<AnnotationMaker>()

    override fun toString() = "${annotationsToString("\n")}${access} ${returnType.name} $name(${getParametersString()}){\n$body\n}"

    open fun getParametersString(): String {
        if (parameters == null) return ""
        return StringBuilder().apply { parameters!!.forEach { append(it).append(",") } }.toString().sout().subStringByLast(1)
    }
}

fun MethodMaker.body(code: String) {
    this.body = code
}

fun MethodMaker.body(body: StringBuilder.() -> Unit) {
    this.body = StringBuilder().apply(body).toString()
}

fun MethodMaker.body(body: () -> String) {
    this.body = body()
}




inline fun <reified T> MethodMaker.returnType() {
    returnType = T::class.java
}

fun MethodMaker.parameters(body: ParametersBuilder.() -> Unit) {
    parameters = ParametersBuilder().apply(body).paraList.toTypedArray()
}

class MethodParameter(
    val name: String,
    val type: Class<*>
) : AnnotationAble {
    override val annotations = arrayListOf<AnnotationMaker>()

    override fun toString() =
        "${annotationsToString(" ")}${type.name} $name"


}

class ParametersBuilder {
    val paraList = arrayListOf<MethodParameter>()

    infix fun String.to(that: Class<*>) {
        paraList.add(MethodParameter(this, that))
    }

    inline fun <reified T> parameter(name: String, body: MethodParameter.() -> Unit) {
        paraList.add(MethodParameter(name, T::class.java).apply(body))
    }

    inline fun <reified T> parameter(name: String) {
        paraList.add(MethodParameter(name, T::class.java))
    }
}
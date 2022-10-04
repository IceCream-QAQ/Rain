package com.IceCreamQAQ.Yu.util.classMaker.ecj

import com.IceCreamQAQ.Yu.toUpperCaseFirstOne
import com.IceCreamQAQ.Yu.util.classMaker.*

class FieldMaker @JvmOverloads constructor(
    val classMaker: ClassMaker,
    val name: String,
    override var access: Access = Access.PUBLIC,
    override var static: Boolean = false,
    override var final: Boolean = false,
    val type: Class<*> = Any::class.java,
    var defaultValue: String? = null
) : AnnotationAble, AccessAble, StaticAble, FinalAble {
    override val annotations = arrayListOf<AnnotationMaker>()

    override fun toString() =
        "${annotationsToString("\n")}${access} ${type.name} $name${defaultValue?.let { " = $it" } ?: ""};"
}

fun FieldMaker.getter() {
    classMaker.method("get${name.toUpperCaseFirstOne()}", returnType = type) {
        body("return this.$name;")
    }
}

fun FieldMaker.setter() {
    classMaker.method("set${name.toUpperCaseFirstOne()}", returnType = type) {
        parameters {
            this@setter.name to type
        }
        body("this.${this@setter.name} = ${this@setter.name};")
    }
}
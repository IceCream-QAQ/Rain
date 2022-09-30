package com.IceCreamQAQ.Yu.util.classMaker

abstract class MField<T>(
    val name: String,
    val fieldType: Class<T>
) : AccessAble, StaticAble, FinalAble, AnnotationAble {

    override var access: Access = Access.PUBLIC
    override var static: Boolean = false
    override var final: Boolean = false

    override val annotations: MutableList<MAnnotation<*>> = ArrayList()
}

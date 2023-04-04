package com.IceCreamQAQ.Yu.util.classMaker

abstract class MMethod(
    val name: String
) : AccessAble, StaticAble, FinalAble, AbstractAble, AnnotationAble {

    abstract val parameters: List<MMethodParameter<*>>
    abstract val returnType: MMethodParameter<*>?

    override var access: Access = Access.PUBLIC
    override var static: Boolean = false
    override var final: Boolean = false
    override var abstract: Boolean = false

    override val annotations: MutableList<MAnnotation<*>> = ArrayList()

}

interface MMethodParameter<T>: AnnotationAble {
    val name: String
    val type: Class<T>
}

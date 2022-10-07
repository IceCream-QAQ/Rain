package com.IceCreamQAQ.Yu.util.classMaker

abstract class MClass<T>(
    val name: String,
    val superClass: Class<T>
) : AccessAble, AbstractAble, StaticAble, FinalAble, AnnotationAble {

    override var access: Access = Access.PUBLIC
    override var abstract: Boolean = false
    override var static: Boolean = false
    override var final: Boolean = false

    abstract val interfaceClass: MutableList<Class<*>>

//    override val annotations: MutableList<MAnnotation<*>>

    abstract val initBlocks: MutableList<out MInitBlock>
    abstract val staticBlocks: MutableList<out MStaticBlock>

    abstract val constructors: MutableList<out MConstructor>
    abstract val fields: MutableList<out MField<*>>
    abstract val methods: MutableList<out MMethod>

    abstract fun make(): Class<out T>
}

abstract class MInitBlock
abstract class MStaticBlock

abstract class MConstructor




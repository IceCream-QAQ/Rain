package com.IceCreamQAQ.Yu.util.classMaker

abstract class MClass<T>(
    val name: String,
    val superClass: Class<T>
) : AccessAble, AbstractAble, StaticAble, FinalAble, AnnotationAble {

    override var access: Access = Access.PUBLIC
    override var abstract: Boolean = false
    override var static: Boolean = false
    override var final: Boolean = false

    val interfaceClass: MutableList<Class<*>> = ArrayList()

    override val annotations: MutableList<MAnnotation<*>> = ArrayList()

    val initBlocks: MutableList<MInitBlock> = ArrayList()
    val staticBlocks: MutableList<MStaticBlock> = ArrayList()

    val constructors: MutableList<MConstructor> = ArrayList()
    val fields: MutableList<MField<*>> = ArrayList()
    val methods: MutableList<MMethod> = ArrayList()

    abstract fun make(): Class<Any>
}

abstract class MInitBlock
abstract class MStaticBlock

abstract class MConstructor




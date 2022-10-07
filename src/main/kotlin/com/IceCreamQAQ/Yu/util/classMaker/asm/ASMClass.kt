package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MClass

    override val interfaceClass: MutableList<Class<*>> = ArrayList()

    override val annotations: MutableList<MAnnotation<*>> = ArrayList()

    override val initBlocks: MutableList<MInitBlock> = ArrayList()
    override val staticBlocks: MutableList<MStaticBlock> = ArrayList()

    override val constructors: MutableList<MConstructor> = ArrayList()
    override val fields: MutableList<ASMField<*>> = ArrayList()
    override val methods: MutableList<ASMMethod> = ArrayList()

    override fun make(): Class<Any> {
        TODO("Not yet implemented")
    }
}
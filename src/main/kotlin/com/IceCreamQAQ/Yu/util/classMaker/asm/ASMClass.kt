package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.util.classMaker.*
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class ASMClass<T>(
    val classloader: IRainClassLoader,
    name: String,
    superClass: Class<T>
) : MClass<T>(name, superClass),
    ASMAnnotationAble {

    override val interfaceClass: MutableList<Class<*>> = ArrayList()

    override val annotations: MutableList<MAnnotation<*>> = ArrayList()

    override val initBlocks: MutableList<MInitBlock> = ArrayList()
    override val staticBlocks: MutableList<MStaticBlock> = ArrayList()

    override val constructors: MutableList<MConstructor> = ArrayList()
    override val fields: MutableList<ASMField<*>> = ArrayList()
    override val methods: MutableList<ASMMethod> = ArrayList()

    override fun make(): Class<out T> {
        val cw = ClassWriter(0)
        cw.visit(
            Opcodes.V1_8,
            countAccess(access, static, final, abstract),
            name,
            null,
            superClass.descriptor,
            interfaceClass.map { it.descriptor }.toTypedArray()
        )

        fields.forEach { it.build(this, cw) }
        methods.forEach { it.build(this, cw) }

        cw.visitEnd()
        return classloader.define(name, cw.toByteArray()) as Class<out T>
    }
}
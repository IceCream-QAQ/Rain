package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.util.classMaker.*
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class ASMClass<T>(
    name: String,
    superClass: Class<T>
) : MClass<T>(name, superClass), ASMAnnotationAble {

    companion object{
        fun <T> ASMClass<T>.make(classLoader: IRainClassLoader): Class<out T> {
            return classLoader.define(name, build()) as Class<out T>
        }

        inline fun <reified T> makeClass(name: String, body: ASMClass<T>.() -> Unit): ASMClass<T> {
            return ASMClass(name, T::class.java).apply(body)
        }
    }

    override val interfaceClass: MutableList<Class<*>> = ArrayList()

    override val annotations: MutableList<MAnnotation<*>> = ArrayList()

    override val initBlocks: MutableList<MInitBlock> = ArrayList()
    override val staticBlocks: MutableList<MStaticBlock> = ArrayList()

    override val constructors: MutableList<MConstructor> = ArrayList()
    override val fields: MutableList<ASMField<*>> = ArrayList()
    override val methods: MutableList<ASMMethod> = ArrayList()

    fun method(name: String, body: ASMMethod.() -> Unit): ASMMethod {
        return ASMMethod(name).apply(body).apply { methods.add(this) }
    }

    fun build(): ByteArray {
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
        return cw.toByteArray()
    }
}
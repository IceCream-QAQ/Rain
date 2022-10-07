package com.IceCreamQAQ.Yu.util.classMaker.asm

import org.objectweb.asm.MethodVisitor


interface ASMMethodWriter {

    fun write(clazz: ASMClass<*>, methodNode: MethodVisitor)


}
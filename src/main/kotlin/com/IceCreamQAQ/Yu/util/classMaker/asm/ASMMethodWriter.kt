package com.IceCreamQAQ.Yu.util.classMaker.asm

import org.objectweb.asm.tree.MethodNode


interface ASMMethodWriter {

    fun write(methodNode: MethodNode): Pair<Int, Int>


}
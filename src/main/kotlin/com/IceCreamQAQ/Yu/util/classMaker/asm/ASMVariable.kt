package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MAnnotation

open class ASMVariable<T>(val type: Class<T>):ASMAnnotationAble {
    override val annotations: MutableList<MAnnotation<*>> = ArrayList()
}
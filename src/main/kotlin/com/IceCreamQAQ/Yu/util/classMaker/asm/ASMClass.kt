package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MClass

class ASMClass<T>(name: String, superClass: Class<T>) : MClass<T>(name, superClass), ASMAnnotationAble {
    override fun make(): Class<Any> {
        TODO("Not yet implemented")
    }
}
package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.Access
import com.IceCreamQAQ.Yu.util.classMaker.Access.*
import jdk.internal.org.objectweb.asm.Type
import org.objectweb.asm.Opcodes.*

fun countAccess(
    access: Access = PUBLIC,
    static: Boolean = false,
    final: Boolean = false,
    abstract: Boolean = false,
    synchronized: Boolean = false
): Int {
    var r = 0

    when (access) {
        PUBLIC -> r += ACC_PUBLIC
        PROTECTED -> r += ACC_PROTECTED
        PRIVATE -> r += ACC_PRIVATE
        DEFAULT -> {}
    }

    if (static) r += ACC_STATIC
    if (final) r += ACC_FINAL
    if (abstract) r += ACC_ABSTRACT
    if (synchronized) r += ACC_SYNCHRONIZED

    return r
}

val Class<*>.descriptor:String get() = Type.getDescriptor(this)
package com.IceCreamQAQ.Yu.util

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

fun getTyped(type: String): String =
    when (type[0]) {
        'B' -> "java/lang/Byte"
        'S' -> "java/lang/Short"
        'I' -> "java/lang/Integer"
        'J' -> "java/lang/Long"
        'F' -> "java/lang/Float"
        'D' -> "java/lang/Double"
        'Z' -> "java/lang/Boolean"
        'C' -> "java/lang/Character"
        else -> type
    }

fun getLoad(type: String): Int =
    when (type[0]) {
        'B', 'S', 'I', 'Z', 'C' -> ILOAD
        'J' -> LLOAD
        'F' -> FLOAD
        'D' -> DLOAD
        else -> ALOAD
    }

fun getReturn(type: String): Int =
    when (type[0]) {
        'B', 'S', 'I', 'Z', 'C' -> IRETURN
        'J' -> LRETURN
        'F' -> FRETURN
        'D' -> DRETURN
        else -> ARETURN
    }

fun makeCast(mv: MethodVisitor, type: String) {
    var type = type
    if (type.length == 1) {
        when (type[0]) {
            'B' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Byte")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false)
            }

            'S' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Short")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false)
            }

            'I' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Integer")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false)
            }

            'J' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Long")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false)
            }

            'F' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Float")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false)
            }

            'D' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Double")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false)
            }

            'Z' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false)
            }

            'C' -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Character")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false)
            }
        }
    } else {
        if (type.startsWith("L")) type = type.substring(1, type.length - 1)
        mv.visitTypeInsn(CHECKCAST, type)
    }
}

fun getTypedWidth(type: String): Int =
    when (type[0]) {
        'B', 'S', 'I', 'Z', 'F', 'C' -> 1
        'J', 'D' -> 2
        else -> ARETURN
    }


class ParamType(
    val type: String,
    val simple: Boolean
)

fun toClassArray(desc: String): List<ParamType> {
    val list = ArrayList<ParamType>()
    var f = false
    var builder = StringBuilder()
    for (c in desc.split("\\)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].substring(1).toCharArray()) {
        if (!f) {
            f = if (!(c == 'L' || c == '[')) {
                list.add(ParamType(getTyped(c.toString()), true))
                continue
            } else true
        }
        builder.append(c)
        if (c == ';') {
            list.add(ParamType(builder.toString(), false))
            builder = StringBuilder()
            f = false
        }
    }
    return list
}

fun toDesc(paramTypes: Array<Class<*>>) =
    StringBuilder().apply {
        paramTypes.forEach {
            append(
                when (it) {
                    Byte::class.javaPrimitiveType -> "B"
                    Short::class.javaPrimitiveType -> "S"
                    Int::class.javaPrimitiveType -> "I"
                    Long::class.javaPrimitiveType -> "J"
                    Float::class.javaPrimitiveType -> "F"
                    Double::class.javaPrimitiveType -> "D"
                    Boolean::class.javaPrimitiveType -> "Z"
                    Char::class.javaPrimitiveType -> "C"
                    else -> it.name.replace(".", "/").let { s -> if (it.isArray) s else "L$s;" }
                }
            )
        }
    }.toString()

fun MethodVisitor.visitIntInsn(num: Int) {
    visitIntInsn(BIPUSH, num)
}

class MethodPara(var stackSize: Int, var stackNum: Int, var type: String)

fun readPara(desc: String, num: Int): List<MethodPara> {
    var num = num
    val paraList = java.util.ArrayList<MethodPara>()
    if (desc.isEmpty()) return paraList
    val s = desc.toCharArray()
    var sb: java.lang.StringBuilder? = null
    for (c in s) {
        if (sb == null) {
            when (c) {
                '[' -> sb = java.lang.StringBuilder("[")
                'L' -> sb = java.lang.StringBuilder()
                else -> {
                    //                        paraList.add(String.valueOf(c));
                    val width = getTypedWidth(c.toString())
                    paraList.add(MethodPara(width, num, c.toString()))
                    num += width
                }
            }
        } else {
            if (c == ';') {
                var p = sb.toString()
                sb = null
                if (p.startsWith("[")) p += ";"
                //                    paraList.add(p);
                paraList.add(MethodPara(1, num, p))
                num += 1
            } else sb.append(c)
        }
    }
    return paraList
}
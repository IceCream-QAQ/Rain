package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MMethod
import com.IceCreamQAQ.Yu.util.classMaker.MMethodParameter
import org.objectweb.asm.ClassVisitor

class ASMMethod(
    name: String
) : MMethod(name), ASMAnnotationAble {

    override val parameters: MutableList<MMethodParameter<*>> = ArrayList()
    override var returnType: MMethodParameter<*>? = null

    var body: ASMMethodWriter? = null
        private set

    fun body(body: ASMMethodWriter): ASMMethod {
        this.body = body
        return this
    }

    fun returnType(type: Class<*>): ASMMethod {
        this.returnType = ASMMethodParameter("", type)
        return this
    }

    inline fun <reified T> parameter(name: String): ASMMethod {
        parameters.add(ASMMethodParameter(name, T::class.java))
        return this
    }

    inline fun <reified T> parameter(name: String, annotationWriter: ASMAnnotationWriter): ASMMethod {
        parameters.add(ASMMethodParameter(name, T::class.java).apply { annotationWriter.write(this) })
        return this
    }

    

    fun build(clazz: ASMClass<*>, visitor: ClassVisitor) {
        val mv = visitor.visitMethod(countAccess(access, static, final, abstract), name, descriptor(), null, null)
        body!!.write(clazz, mv)
    }

    private fun descriptor(): String {
        val sb = StringBuilder("(")
        parameters.forEach { sb.append(it.type.descriptor) }
        sb.append(")")
        returnType?.also { sb.append(it.type.descriptor) } ?: sb.append("V")
        return sb.toString()
    }

}

class ASMMethodParameter<T>(override val name: String, type: Class<T>) : ASMVariable<T>(type), MMethodParameter<T>
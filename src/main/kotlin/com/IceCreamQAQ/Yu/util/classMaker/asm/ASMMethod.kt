package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MMethod
import com.IceCreamQAQ.Yu.util.classMaker.MMethodParameter
import org.objectweb.asm.ClassVisitor

class ASMMethod(
    name: String
) : MMethod(name), ASMAnnotationAble {

    companion object {
        inline fun <reified T> ASMMethod.parameter(name: String) = parameter(name, T::class.java)
        inline fun <reified T> ASMMethod.parameter(name: String, block: ASMMethodParameter<T>.() -> Unit): ASMMethod {
            parameters.add(ASMMethodParameter(name, T::class.java).apply(block))
            return this
        }
    }

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

    fun parameter(name: String, type: Class<*>): ASMMethod {
        parameters.add(ASMMethodParameter(name, type))
        return this
    }

    fun parameter(name: String, type: Class<*>, annotationWriter: ASMAnnotationWriter): ASMMethod {
        parameters.add(ASMMethodParameter(name, type).apply { annotationWriter.write(this) })
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

class ASMMethodParameter<T>(name: String, type: Class<T>) : MMethodParameter<T>(name, type), ASMAnnotationAble
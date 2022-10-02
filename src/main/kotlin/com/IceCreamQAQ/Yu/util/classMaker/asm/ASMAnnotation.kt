package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.MAnnotation
import jdk.internal.org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode

class ASMAnnotation<T>(annotationType: Class<out Annotation>) : MAnnotation<T>(annotationType) {

    val annotationValues: MutableList<Pair<String, Any>> = ArrayList()

    fun build() =
        AnnotationNode(Type.getDescriptor(annotationType)).apply {
            values = ArrayList<Any?>().apply {
                annotationValues.forEach { (k, v) ->
                    add(k)
                    add(v)
                }
            }
        }

    override fun add(key: String, value: Any) {
        annotationValues.add(key to value)
    }

}


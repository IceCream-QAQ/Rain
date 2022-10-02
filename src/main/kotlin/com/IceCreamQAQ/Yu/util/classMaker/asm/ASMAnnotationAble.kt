package com.IceCreamQAQ.Yu.util.classMaker.asm

import com.IceCreamQAQ.Yu.util.classMaker.AnnotationAble

interface ASMAnnotationAble : AnnotationAble {

    fun <T : Annotation> addAnnotation(annotation: ASMAnnotation<T>): ASMAnnotation<T> {
        annotations.add(annotation)
        return annotation
    }

    override fun <T : Annotation> annotation(type: Class<T>): ASMAnnotation<T> =
        addAnnotation(ASMAnnotation(type))

    override fun <T : Annotation> annotation(type: Class<T>, values: Map<String, Any>): ASMAnnotation<T> =
        addAnnotation(ASMAnnotation<T>(type).apply { values.forEach { (k, v) -> add(k, v) } })
    
}
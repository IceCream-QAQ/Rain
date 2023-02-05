package com.IceCreamQAQ.Yu.validation.global

import com.IceCreamQAQ.Yu.hasAnnotation
import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer
import com.IceCreamQAQ.Yu.validation.NoValidHook
import com.IceCreamQAQ.Yu.validation.ValidateBy
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

class ValidHookAddTransformer(
    val classloader: IRainClassLoader
) : ClassTransformer {


    override fun transform(node: ClassNode, className: String): Boolean {
        val needAddAnnotationMethod = node.methods
            .filter {
                it.visibleAnnotations == null ||
                        it.visibleAnnotations.any { a ->
                            kotlin.runCatching {
                                !classloader.forName(
                                    a.desc
                                        .substring(1, a.desc.length - 1)
                                        .replace("/", "."),
                                    false
                                ).hasAnnotation<NoValidHook>()
                            }.getOrElse { true }
                        }
            }
            .filter {
                !(it.visibleAnnotations?.any { a -> a.desc == "Lcom/IceCreamQAQ/Yu/validation/ValidHook" } ?: false)
            }
            .filter {
                it.visibleParameterAnnotations?.any { p ->
                    p?.any { a ->
                        kotlin.runCatching {
                            classloader.forName(
                                a.desc
                                    .substring(1, a.desc.length - 1)
                                    .replace("/", "."),
                                false
                            ).hasAnnotation<ValidateBy>()
                        }.getOrElse { false }
                    } ?: false
                } ?: false
            }

        if (needAddAnnotationMethod.isEmpty()) return false
        needAddAnnotationMethod.forEach {
            if (it.visibleAnnotations == null) it.visibleAnnotations = arrayListOf()
            it.visibleAnnotations.add(
                AnnotationNode("Lcom/IceCreamQAQ/Yu/validation/ValidHook;")
            )
        }
//        if (validationMethods.isEmpty()) return false


        return true
    }

}
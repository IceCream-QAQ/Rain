package rain.validator.global

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import rain.classloader.IRainClassLoader
import rain.classloader.transformer.ClassTransformer
import rain.function.hasAnnotation
import rain.validator.NoValidHook
import rain.validator.ValidHook
import rain.validator.ValidateBy

/** Adds [ValidHook] to methods that have parameter validation annotations. */
class ValidHookAddTransformer(
    private val classloader: IRainClassLoader,
) : ClassTransformer {

    override fun transform(node: ClassNode, className: String): Boolean {
        val needAddAnnotationMethod = node.methods.filter { method ->
            val annotations = method.visibleAnnotations
            val allowsValidationHook = annotations == null || annotations.any { annotation ->
                runCatching {
                    !classloader.forName(annotationClassName(annotation.desc), false)
                        .hasAnnotation<NoValidHook>()
                }.getOrElse { true }
            }
            val alreadyHasValidationHook =
                annotations?.any { it.desc == ValidHook::class.java.descriptor() } ?: false
            val hasValidationParameter = method.visibleParameterAnnotations?.any { parameterAnnotations ->
                parameterAnnotations?.any { annotation ->
                    runCatching {
                        classloader.forName(annotationClassName(annotation.desc), false)
                            .hasAnnotation<ValidateBy>()
                    }.getOrElse { false }
                } ?: false
            } ?: false

            allowsValidationHook && !alreadyHasValidationHook && hasValidationParameter
        }

        if (needAddAnnotationMethod.isEmpty()) return false
        needAddAnnotationMethod.forEach { method ->
            if (method.visibleAnnotations == null) method.visibleAnnotations = arrayListOf()
            method.visibleAnnotations.add(AnnotationNode(ValidHook::class.java.descriptor()))
        }
        return true
    }

    private fun annotationClassName(descriptor: String): String =
        descriptor.substring(1, descriptor.length - 1).replace('/', '.')

    private fun Class<out Annotation>.descriptor(): String =
        "L${name.replace('.', '/')};"

}

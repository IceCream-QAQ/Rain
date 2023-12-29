package rain.classloader.enchant

import rain.classloader.IRainClassLoader
import org.objectweb.asm.tree.ClassNode
import rain.classloader.transformer.ClassTransformer

class EnchantManager(private val classloader: IRainClassLoader) : ClassTransformer {
    override fun transform(node: ClassNode, className: String): Boolean {
        if (node.visibleAnnotations != null) {
            var en = false

            for (annotation in node.visibleAnnotations) {
                val annotationClassName =
                    annotation.desc.substring(1, annotation.desc.length - 1).replace("/", ".")
                val annotationClass = classloader.forName(annotationClassName, false)
                val aa = annotationClass.annotations
                for (a in aa) {
                    if (a is EnchantBy) {
                        en = true
                        val enchanter = a.value.java.newInstance()
                        enchanter.enchantClass(node)
                    }
                }
            }
            return en
        }
        return false
    }
}

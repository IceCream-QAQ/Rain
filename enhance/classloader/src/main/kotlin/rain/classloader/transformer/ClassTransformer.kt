package rain.classloader.transformer

import org.objectweb.asm.tree.ClassNode

interface ClassTransformer {
    fun transform(node: ClassNode, className: String): Boolean
}

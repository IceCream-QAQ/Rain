package rain.classloader.enchant

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

interface Enchanter {
    fun enchantClass(cn: ClassNode): Boolean = false
    fun enchantMethod(cn: ClassNode, mn: MethodNode): Boolean = false
}

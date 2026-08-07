package rain.classloader.enchant

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LocalVariableNode
import org.objectweb.asm.tree.MethodNode

class MethodParaNamedEnchanter : Enchanter {

    @EnchantBy(MethodParaNamedEnchanter::class)
    annotation class MethodParaNamed

    override fun enchantClass(cn: ClassNode): Boolean {
        cn.visibleAnnotations?.find { it.desc == "Lkotlin/Metadata;" }?.let { return false }

        var write = false
        for (method in cn.methods) {
            if (method.name == "<init>") continue

            if (method.localVariables.size < 2) continue

            val paraName: MutableList<String> = ArrayList()

            var thisNode: LocalVariableNode? = null

            for (localVariable in method.localVariables) {
                if (localVariable.name == "this") {
                    thisNode = localVariable
                    break
                }
            }

            if (thisNode == null) continue

            for (localVariable in method.localVariables) {
                if (localVariable.name == "this") continue

                if (thisNode.start === localVariable.start && thisNode.end === localVariable.end)
                    paraName.add(localVariable.name)
            }

            var changed = false

            fun writeAnnotation(na: MutableList<AnnotationNode>, i: Int) {
                val an = AnnotationNode("Ljavax/inject/Named;")
                val values: MutableList<Any> = ArrayList()

                values.add("value")
                values.add(paraName[i])

                an.values = values

                na.add(an)
                changed = true
                write = true
            }

            var pas = method.visibleParameterAnnotations
            if (pas == null) pas = arrayOfNulls<MutableList<AnnotationNode>>(paraName.size)

            for (i in pas.indices) {
                val ans = pas[i]

                if (ans == null) {
                    val na: MutableList<AnnotationNode> = ArrayList()
                    pas[i] = na
                    writeAnnotation(na, i)
                } else {
                    var haveNamed = false
                    for (an in ans) {
                        if (an.desc != "Ljavax/inject/Named;") continue
                        haveNamed = true
                        break
                    }

                    if (haveNamed) continue

                    writeAnnotation(ans, i)
                }
            }
            if (changed) method.visibleParameterAnnotations = pas
        }
        return write
    }
}

package rain.classloader.enchant

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LocalVariableNode
import org.objectweb.asm.tree.MethodNode

class MethodParaNamedEnchanter : Enchanter {
    override fun enchantClass(cn: ClassNode) {
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

            if (thisNode == null) return

            for (localVariable in method.localVariables) {
                if (localVariable.name == "this") continue

                if (thisNode.start === localVariable.start && thisNode.end === localVariable.end) paraName.add(
                    localVariable.name
                )
            }

            var pas = method.visibleParameterAnnotations
            if (pas == null) pas = arrayOfNulls<MutableList<AnnotationNode>>(paraName.size)
            method.visibleParameterAnnotations = pas

            for (i in pas.indices) {
                val ans = pas[i]

                if (ans == null) {
                    val na: MutableList<AnnotationNode> = ArrayList()
                    pas[i] = na

                    val an = AnnotationNode("Ljavax/inject/Named;")
                    val values: MutableList<Any> = ArrayList()

                    values.add("value")
                    values.add(paraName[i])

                    an.values = values

                    na.add(an)
                } else {
                    var haveNamed = false
                    for (an in ans) {
                        if (an.desc != "Ljavax/inject/Named;") continue
                        haveNamed = true
                        break
                    }

                    if (haveNamed) continue

                    val an = AnnotationNode("Ljavax/inject/Named;")
                    val values: MutableList<Any> = ArrayList()
                    values.add("value")
                    values.add(paraName[i])
                    an.values = values
                    ans.add(an)
                }
            }
        }
    }

    override fun enchantMethod(cn: ClassNode, mn: MethodNode) {
    }
}

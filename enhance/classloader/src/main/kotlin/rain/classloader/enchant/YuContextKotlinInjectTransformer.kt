package rain.classloader.enchant

import org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.GETFIELD
import org.objectweb.asm.Opcodes.PUTFIELD
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode
import rain.classloader.transformer.ClassTransformer

class YuContextKotlinInjectTransformer : ClassTransformer {

    companion object {
        const val BACK_FIELD_NAME = "rain_yu_context_kotlin_by_inject_back_field"
        const val INJECT_WRAPPER_DESC = "rain/di/kotlin/YuContextKotlinInjectReadWriteProperty"
        const val CONFIG_WRAPPER_DESC = "rain/di/kotlin/YuContextKotlinConfigInjectReadWriteProperty"
        const val INTERFACE_DESC = "rain/di/kotlin/YuContextKotlinInjectBase"
        const val INTERFACE_GETTER_NAME = "getContext_Rain_YuContext_Kotlin_ByInject"
        const val INTERFACE_SETTER_NAME = "setContext_Rain_YuContext_Kotlin_ByInject"
        const val YU_CONTEXT_DESC = "Lrain/di/YuContext;"
    }

    override fun transform(node: ClassNode, className: String): Boolean {
        node.methods.filter { it.name == "<init>" }
            .any { method ->
                method.instructions.any {
                    it is TypeInsnNode && (it.desc == INJECT_WRAPPER_DESC || it.desc == CONFIG_WRAPPER_DESC)
                }
            }
            .let { if (!it) return false }

        node.apply { if (interfaces == null) interfaces = arrayListOf() }
            .interfaces.add(INTERFACE_DESC)

        node.fields.add(
            FieldNode(
                ACC_PRIVATE,
                BACK_FIELD_NAME,
                YU_CONTEXT_DESC,
                null,
                null
            )
        )
        node.methods.add(
            MethodNode(
                ACC_PUBLIC,
                INTERFACE_GETTER_NAME,
                "()$YU_CONTEXT_DESC",
                null,
                null
            ).apply {
                visitCode()
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(
                    GETFIELD,
                    className.replace(".", "/"),
                    BACK_FIELD_NAME,
                    YU_CONTEXT_DESC
                )
                visitInsn(ARETURN)
                visitMaxs(1, 1)
                visitEnd()
            }
        )
        node.methods.add(
            MethodNode(
                ACC_PUBLIC,
                INTERFACE_SETTER_NAME,
                "($YU_CONTEXT_DESC)V",
                null,
                null
            ).apply {
                visibleAnnotations = arrayListOf(AnnotationNode("Ljavax/inject/Inject;"))
                visitCode()
                visitVarInsn(ALOAD, 0)
                visitVarInsn(ALOAD, 1)
                visitFieldInsn(
                    PUTFIELD,
                    className.replace(".", "/"),
                    BACK_FIELD_NAME,
                    YU_CONTEXT_DESC
                )
                visitInsn(RETURN)
                visitMaxs(2, 2)
                visitEnd()
            }
        )
        return true
    }

}
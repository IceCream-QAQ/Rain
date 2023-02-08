package com.IceCreamQAQ.Yu.di.kotlin

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface YuContextKotlinInjectBase {
    fun getContext_Rain_YuContext_Kotlin_ByInject(): YuContext
}

class YuContextKotlinInjectReadWriteProperty<T>(val name: String, val type: Class<*>) : ReadWriteProperty<Any, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value == null) {
            value = (thisRef as YuContextKotlinInjectBase)
                .getContext_Rain_YuContext_Kotlin_ByInject()
                .getBean(type, name) as? T?
        }
        return value!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

class YuContextKotlinConfigInjectReadWriteProperty<T>(val name: String, val type: Class<*>) :
    ReadWriteProperty<Any, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value == null) {
            value = (thisRef as YuContextKotlinInjectBase)
                .getContext_Rain_YuContext_Kotlin_ByInject()
                .configManager
                .getConfig(name, type) as? T?
        }
        return value!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

class YuContextKotlinInjectTransformer : ClassTransformer {
    override fun transform(node: ClassNode, className: String): Boolean {
        node.methods.filter { it.name == "<init>" }
            .any { method ->
                method.instructions.any {
                    it is TypeInsnNode &&
                            (it.desc == "com/IceCreamQAQ/Yu/di/kotlin/YuContextKotlinInjectReadWriteProperty" ||
                                    it.desc == "com/IceCreamQAQ/Yu/di/kotlin/YuContextKotlinConfigInjectReadWriteProperty")
                }
            }
            .let { if (!it) return false }

        node.apply { if (interfaces == null) interfaces = arrayListOf() }
            .interfaces.add("com/IceCreamQAQ/Yu/di/kotlin/YuContextKotlinInjectBase")

        node.fields.add(
            FieldNode(
                ACC_PRIVATE,
                "rain_yu_context_kotlin_by_inject_back_field",
                "Lcom/IceCreamQAQ/Yu/di/YuContext;",
                null,
                null
            )
        )
        node.methods.add(
            MethodNode(
                ACC_PUBLIC,
                "getContext_Rain_YuContext_Kotlin_ByInject",
                "()Lcom/IceCreamQAQ/Yu/di/YuContext;",
                null,
                null
            ).apply {
                visitCode()
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(
                    GETFIELD,
                    className.replace(".", "/"),
                    "rain_yu_context_kotlin_by_inject_back_field",
                    "Lcom/IceCreamQAQ/Yu/di/YuContext;"
                )
                visitInsn(ARETURN)
                visitMaxs(1, 1)
                visitEnd()
            }
        )
        node.methods.add(
            MethodNode(
                ACC_PUBLIC,
                "setContext_Rain_YuContext_Kotlin_ByInject",
                "(Lcom/IceCreamQAQ/Yu/di/YuContext;)V",
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
                    "rain_yu_context_kotlin_by_inject_back_field",
                    "Lcom/IceCreamQAQ/Yu/di/YuContext;"
                )
                visitInsn(RETURN)
                visitMaxs(2, 2)
                visitEnd()
            }
        )
        return true
    }

}

inline fun <reified T> inject(name: String = YuContext.defaultInstanceName): ReadWriteProperty<Any, T> =
    YuContextKotlinInjectReadWriteProperty(name, T::class.java)

inline fun <reified T> config(name: String = YuContext.defaultInstanceName): ReadWriteProperty<Any, T> =
    YuContextKotlinConfigInjectReadWriteProperty(name, T::class.java)
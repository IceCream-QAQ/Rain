package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.Event.Weight
import com.IceCreamQAQ.Yu.loader.SpawnClassLoader
import org.objectweb.asm.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import javax.inject.Inject

class EventInvokerCreator(
    val classLoader: SpawnClassLoader
) {
    private var num = 0

    fun createEventHandlerInvokerClass(method: Method): Class<*> {
        val cw = ClassWriter(0)
        var mv: MethodVisitor

        val isStatic = Modifier.isStatic(method.modifiers)
        val name = getUniqueName(method)
        val desc = name.replace('.', '/')
        val instType = Type.getInternalName(method.declaringClass)
        val eventType = Type.getInternalName(method.parameterTypes[0])


        cw.visit(
            Opcodes.V1_6,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER,
            desc,
            null,
            "java/lang/Object",
            arrayOf(listererClassName)
        )

        cw.visitSource("YuCoreAutoCreatedEventInvoker.java", null)
        run {
            if (!isStatic) cw.visitField(Opcodes.ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd()
        }
        run {
            mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                if (isStatic) "()V" else "(Ljava/lang/Object;)V",
                null,
                null
            )
            mv.visitCode()
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            if (!isStatic) {
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitVarInsn(Opcodes.ALOAD, 1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, desc, "instance", "Ljava/lang/Object;")
            }
            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(2, 2)
            mv.visitEnd()
        }
        run {
            val paraClass = Type.getInternalName(method.parameterTypes[0])
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null)
            mv.visitCode()
            val label0 = Label()
            mv.visitLabel(label0)
            mv.visitLineNumber(16, label0)
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitTypeInsn(Opcodes.INSTANCEOF, paraClass)
            val label1 = Label()
            mv.visitJumpInsn(Opcodes.IFNE, label1)
            mv.visitInsn(Opcodes.RETURN)
            mv.visitLabel(label1)
            mv.visitLineNumber(17, label1)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            if (!isStatic) {
                mv.visitFieldInsn(Opcodes.GETFIELD, desc, "instance", "Ljava/lang/Object;")
                mv.visitTypeInsn(Opcodes.CHECKCAST, instType)
            }
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitTypeInsn(Opcodes.CHECKCAST, eventType)
            mv.visitMethodInsn(
                if (isStatic) Opcodes.INVOKESTATIC else Opcodes.INVOKEVIRTUAL,
                instType,
                method.name,
                Type.getMethodDescriptor(method),
                false
            )
            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(2, 2)
            mv.visitEnd()
        }
        cw.visitEnd()
        val ret = classLoader.define(name, cw.toByteArray())
        return ret
    }


    private fun getUniqueName(callback: Method): String {
        return String.format(
            "Yu_EventHandlerClass_%d_%s_%s_%s_IceCreamQAQ_OpenSource_YuFramework",
            num++,
            callback.declaringClass.simpleName,
            callback.name,
            callback.parameterTypes[0].simpleName
        )
    }

    companion object {
        private val listererClassName: String = Type.getInternalName(EventInvoker::class.java)
        private val HANDLER_FUNC_DESC: String = Type.getMethodDescriptor(EventInvoker::class.java.declaredMethods[0])
    }
}

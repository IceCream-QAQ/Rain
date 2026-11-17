package rain.validator.obj

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ACC_FINAL
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_SUPER
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.CHECKCAST
import org.objectweb.asm.Opcodes.GETFIELD
import org.objectweb.asm.Opcodes.INVOKEINTERFACE
import org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.Opcodes.V1_8
import org.objectweb.asm.Type
import rain.classloader.IRainClassLoader
import java.lang.reflect.Field
import java.lang.reflect.Method

object FieldReaderCreator {

    private var index: Int = 0
    const val FIELD_READER_INTERNAL_NAME = "rain/validator/obj/FieldReader"
    const val READ_DESCRIPTOR = "(Ljava/lang/Object;)Ljava/lang/Object;"

    fun fieldReader(clazz: Class<*>, field: Field, classLoader: IRainClassLoader): Class<out FieldReader> {
        val generatedName = "${clazz.name}\$RAIN_VALIDATOR_FIELD_READER$${field.name}_${index++}"
        val bytecode = createFieldReader(clazz, field, null, generatedName)
        return classLoader.define(generatedName, bytecode) as Class<out FieldReader>
    }

    fun getterReader(clazz: Class<*>, field: Field, getter: Method, classLoader: IRainClassLoader): Class<out FieldReader> {
        val generatedName = "${clazz.name}\$RAIN_VALIDATOR_FIELD_READER$${field.name}_${index++}"
        val bytecode = createFieldReader(clazz, field, getter, generatedName)
        return classLoader.define(generatedName, bytecode) as Class<out FieldReader>
    }

    fun createFieldReader(clazz: Class<*>, field: Field, getter: Method?, generatedName: String): ByteArray {
        val generatedInternalName = generatedName.replace('.', '/')
        val targetInternalName = Type.getInternalName(clazz)

        val writer = ClassWriter(0)
        writer.visit(
            V1_8,
            ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
            generatedInternalName,
            null,
            "java/lang/Object",
            arrayOf(FIELD_READER_INTERNAL_NAME),
        )

        // 构造函数
        writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
            .apply {
                visitCode()
                visitVarInsn(ALOAD, 0)
                visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
                visitInsn(RETURN)
                visitMaxs(1, 1)
                visitEnd()
            }

        // read 实现
        writer.visitMethod(
            ACC_PUBLIC,
            "read",
            READ_DESCRIPTOR,
            null,
            null,
        ).apply {
            visitCode()
            visitVarInsn(ALOAD, 1)
            visitTypeInsn(CHECKCAST, targetInternalName)

            getter?.let {
                visitMethodInsn(
                    if (clazz.isInterface) INVOKEINTERFACE else INVOKEVIRTUAL,
                    Type.getInternalName(clazz),
                    it.name,
                    Type.getMethodDescriptor(getter),
                    clazz.isInterface,
                )
            } ?: visitFieldInsn(
                GETFIELD,
                Type.getInternalName(clazz),
                field.name,
                Type.getDescriptor(field.type),
            )
            val valueType = Type.getType(getter?.returnType ?: field.type)
            box(this, valueType)
            visitInsn(ARETURN)
            visitMaxs(if (valueType.sort == Type.LONG || valueType.sort == Type.DOUBLE) 2 else 1, 2)
            visitEnd()
        }

        writer.visitEnd()
        return writer.toByteArray()
    }

    private fun box(method: MethodVisitor, type: Type) {
        when (type.sort) {
            Type.BOOLEAN -> valueOf(method, "java/lang/Boolean", "(Z)Ljava/lang/Boolean;")
            Type.BYTE -> valueOf(method, "java/lang/Byte", "(B)Ljava/lang/Byte;")
            Type.CHAR -> valueOf(method, "java/lang/Character", "(C)Ljava/lang/Character;")
            Type.SHORT -> valueOf(method, "java/lang/Short", "(S)Ljava/lang/Short;")
            Type.INT -> valueOf(method, "java/lang/Integer", "(I)Ljava/lang/Integer;")
            Type.FLOAT -> valueOf(method, "java/lang/Float", "(F)Ljava/lang/Float;")
            Type.LONG -> valueOf(method, "java/lang/Long", "(J)Ljava/lang/Long;")
            Type.DOUBLE -> valueOf(method, "java/lang/Double", "(D)Ljava/lang/Double;")
        }
    }

    private fun valueOf(method: MethodVisitor, owner: String, descriptor: String) {
        method.visitMethodInsn(INVOKESTATIC, owner, "valueOf", descriptor, false)
    }
}

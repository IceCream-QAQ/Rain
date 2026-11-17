package rain.validator.obj

import rain.classloader.IRainClassLoader
import rain.function.getMethodOrNull
import rain.function.isPublic
import rain.function.toUpperCaseFirstOne
import java.lang.reflect.Field
import java.lang.reflect.Modifier

interface FieldReader {
    fun read(instance: Any): Any?

    class ReflectFieldReader(private val field: Field) : FieldReader {
        override fun read(instance: Any): Any? {
            field.isAccessible = true
            return field[instance]
        }
    }

    companion object{
        fun fieldReaderOf(clazz: Class<*>, field: Field, classloader: IRainClassLoader?): FieldReader {
            if (classloader == null) return ReflectFieldReader(field)
            val isPublicField = field.isPublic
            val getterMethod = run {
                field.takeIf { field.type == Boolean::class.javaPrimitiveType || field.type == Boolean::class.javaObjectType }
                    ?.let { clazz.getMethodOrNull("is${field.name.toUpperCaseFirstOne()}") }
                    ?: clazz.getMethodOrNull("get${field.name.toUpperCaseFirstOne()}")
            }?.takeIf { it.parameterCount == 0 && !Modifier.isStatic(it.modifiers) && it.returnType != Void.TYPE }

            if (!isPublicField && getterMethod == null) return ReflectFieldReader(field)
            val readerClass = getterMethod?.let { FieldReaderCreator.getterReader(clazz, field, it, classloader) }
                ?: FieldReaderCreator.fieldReader(clazz, field, classloader)
            return readerClass.getDeclaredConstructor().newInstance()
        }
    }

}
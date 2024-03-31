package rain.function

import java.lang.reflect.*


inline fun <reified T : Annotation> Method.annotation(body: T.() -> Unit) = getAnnotation(T::class.java)?.apply(body)

val Executable.fullName: String
    get() {
        val nameBuilder = StringBuilder(this.declaringClass.name).append(".").append(this.name).append("(")
        val max = parameterCount - 1
        for ((i, clazz) in parameterTypes.withIndex()) {
            nameBuilder.append(clazz.simpleName)
            if (i < max) nameBuilder.append(", ")
        }
        nameBuilder.append(")")
        return nameBuilder.toString()
    }

val Executable.nameWithParams: String
    inline get() = StringBuilder(name)
        .append("(")
        .apply {
            parameterTypes.let { parameterTypes ->
                val max = parameterTypes.size - 1
                parameterTypes.forEachIndexed { i, it ->
                    append(it.simpleName)
                    if (i < max) append(", ")
                }
            }
        }.append(")")
        .toString()

val Constructor<*>.nameWithParamsFullClass: String
    inline get() = StringBuilder(declaringClass.name)
        .append("(")
        .apply {
            parameterTypes.let { parameterTypes ->
                val max = parameterTypes.size - 1
                parameterTypes.forEachIndexed { i, it ->
                    append(it.name)
                    if (i < max) append(", ")
                }
            }
        }.append(")")
        .toString()

val Method.nameWithParamsFullClass: String
    inline get() = StringBuilder(declaringClass.name)
        .append(".")
        .append(name)
        .append("(")
        .apply {
            parameterTypes.let { parameterTypes ->
                val max = parameterTypes.size - 1
                parameterTypes.forEachIndexed { i, it ->
                    append(it.name)
                    if (i < max) append(", ")
                }
            }
        }.append(")")
        .toString()

val Member.isExecutable get() = !isStatic && !isAbstract
val Member.isAbstract get() = Modifier.isAbstract(modifiers)
val Member.isStatic get() = Modifier.isStatic(modifiers)
val Member.isFinal get() = Modifier.isFinal(modifiers)
val Member.isPrivate get() = Modifier.isPrivate(modifiers)
val Member.isProtected get() = Modifier.isProtected(modifiers)
val Member.isPublic get() = Modifier.isPublic(modifiers)

val Class<*>.allField: List<Field>
    get() {
        val list = ArrayList<Field>()
        var clazz: Class<*>? = this
        while (clazz != null) {
            list.addAll(clazz.declaredFields)
            clazz = clazz.superclass
        }
        return list
    }

val Class<*>.allMethod: List<Method>
    get() {
        val list = ArrayList<Method>()
        var clazz: Class<*>? = this
        while (clazz != null) {
            list.addAll(clazz.declaredMethods)
            clazz = clazz.superclass
        }
        return list
    }

inline fun <reified T : Annotation> AnnotatedElement.hasAnnotation(): Boolean =
    getAnnotation(T::class.java)?.let { true } ?: false

inline fun <reified T : Annotation> AnnotatedElement.annotation(): T? = getAnnotation(T::class.java)
inline fun <reified T : Annotation> AnnotatedElement.annotation(body: T.() -> Unit): T? =
    getAnnotation(T::class.java)?.apply(body)

inline fun <reified T : Annotation> Annotation.annotationAnnotation(): List<T> {
    val list = ArrayList<T>()
    this::class.java.interfaces.forEach {
        list.addAll(it.getAnnotationsByType(T::class.java))
    }
    return list
}
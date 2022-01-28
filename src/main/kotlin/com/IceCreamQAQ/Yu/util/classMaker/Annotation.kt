package com.IceCreamQAQ.Yu.util.classMaker

import com.IceCreamQAQ.Yu.util.subStringByLast

interface AnnotationAble {
    val annotations: MutableList<AnnotationMaker>

    fun annotationsToString(separator: String): String {
        val sb = StringBuilder()
        annotations.forEach {
            sb.append("@${it.type.name}")
            it.body?.let { body -> sb.append("($body)") }
            sb.append(separator)
        }
        return sb.toString()
    }
}

fun AnnotationAble.annotation(type: Class<out Annotation>) {
    annotations.add(AnnotationMaker(type))
}

inline fun <reified T : Annotation> AnnotationAble.annotation() = annotation(T::class.java)

fun AnnotationAble.annotation(type: Class<out Annotation>, value: String) {
    annotations.add(AnnotationMaker(type, value))
}

inline fun <reified T : Annotation> AnnotationAble.annotation(value: String) = annotation(T::class.java, value)

fun AnnotationAble.annotation(type: Class<out Annotation>, value: Map<String, Any>) {
    annotations.add(
        AnnotationMaker(
            type,
            StringBuilder("(")
                .apply { value.forEach { (k, v) -> append("$k = $v,") } }
                .append(")")
                .toString()
                .subStringByLast(1..2)
        )
    )
}

inline fun <reified T : Annotation> AnnotationAble.annotation(value: Map<String, Any>) =
    annotation(T::class.java, value)

fun AnnotationAble.annotation(type: Class<out Annotation>, body: AnnotationBodyBuilder.() -> Unit) {
    annotations.add(
        AnnotationMaker(
            type,
            AnnotationBodyBuilder().apply(body).toString()
        )
    )
}

inline fun <reified T : Annotation> AnnotationAble.annotation(noinline body: AnnotationBodyBuilder.() -> Unit) =
    annotation(T::class.java, body)

class AnnotationMaker(
    val type: Class<*> = Any::class.java,
    var body: String? = null
)

class AnnotationBodyBuilder {
    private val builder = StringBuilder()

    infix fun String.to(that: Any) {
        builder.append(this).append(" = ").append(that).append(",")
    }

    override fun toString() = builder.toString().subStringByLast(1)
}
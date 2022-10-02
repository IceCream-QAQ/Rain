package com.IceCreamQAQ.Yu.util.classMaker

enum class Access(val value: String) {
    PUBLIC("public"), DEFAULT(""), PROTECTED("protected"), PRIVATE("private")
}

interface AccessAble {
    var access: Access

    fun private() {
        this.access = Access.PRIVATE
    }

    fun protected() {
        this.access = Access.PROTECTED
    }

    fun public() {
        this.access = Access.PUBLIC
    }
}

interface StaticAble {
    var static: Boolean

    fun static() {
        this.static = true
    }
}

interface FinalAble {
    var final: Boolean

    fun final() {
        this.final = true
    }

}

interface AbstractAble {
    var abstract: Boolean

    fun abstract() {
        this.abstract = true
    }
}

interface AnnotationAble {

    companion object {
        inline fun <reified T : Annotation> AnnotationAble.annotation() = this.annotation(T::class.java)
        inline fun <reified T : Annotation> AnnotationAble.annotation(values: Map<String, Any>) =
            this.annotation(T::class.java, values)

        inline fun <reified T : Annotation> AnnotationAble.annotation(block: Annotation.() -> Unit) =
            this.annotation(T::class.java).apply(block)
    }

    val annotations: MutableList<MAnnotation<*>>

    fun annotation(type: Class<out Annotation>): Annotation

    fun annotation(type: Class<out Annotation>, values: Map<String, Any>): Annotation

}
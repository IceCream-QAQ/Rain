package com.IceCreamQAQ.Yu.util.classMaker

enum class Access {
    PUBLIC {
        override fun toString() = "public"
    },
    DEFAULT {
        override fun toString() = ""
    },
    PROTECTED {
        override fun toString() = "protected"
    },
    PRIVATE {
        override fun toString() = "private"
    }
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

        inline fun <reified T : Annotation> AnnotationAble.annotation(block: MAnnotation<T>.() -> Unit): MAnnotation<T> =
            this.annotation(T::class.java).apply(block)
    }

    val annotations: MutableList<MAnnotation<*>>

    fun <T : Annotation> annotation(type: Class<T>): MAnnotation<T>

    fun <T : Annotation> annotation(type: Class<T>, values: Map<String, Any>): MAnnotation<T>

}
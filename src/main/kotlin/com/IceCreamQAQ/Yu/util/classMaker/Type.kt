package com.IceCreamQAQ.Yu.util.classMaker

import com.IceCreamQAQ.Yu.util.classMaker.ecj.AnnotationBodyBuilder
import com.IceCreamQAQ.Yu.util.classMaker.ecj.AnnotationMaker
import com.IceCreamQAQ.Yu.util.subStringByLast

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

package com.IceCreamQAQ.Yu.util.classMaker

enum class Access(val value: String) {
    PUBLIC("public"), DEFAULT(""), PROTECTED("protected"), PRIVATE("private")
}

interface AccessAble {
    var access: Access
}

fun AccessAble.private() {
    this.access = Access.PRIVATE
}

fun AccessAble.protected() {
    this.access = Access.PROTECTED
}

fun AccessAble.public() {
    this.access = Access.PUBLIC
}

interface StaticAble {
    var static: Boolean
}

fun StaticAble.static() {
    this.static = true
}

interface FinalAble {
    var final: Boolean
}

fun FinalAble.final() {
    this.final = true
}

interface AbstractAble{
    var abstract: Boolean
}

fun AbstractAble.abstract() {
    this.abstract = true
}
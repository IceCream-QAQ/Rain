package com.IceCreamQAQ.Yu.loader

class LoadItem_() {

    lateinit var annotation: Annotation
    lateinit var type: Class<*>

    constructor(annotation: Annotation, type: Class<*>) : this() {
        this.annotation = annotation
        this.type = type
    }
}
package com.IceCreamQAQ.Yu.loader

class LoadItem() {

    lateinit var annotation: Annotation
    lateinit var type: Class<*>
    lateinit var instance: Any

    constructor(annotation: Annotation, type: Class<*>, instance: Any) : this() {
        this.annotation = annotation
        this.type = type
        this.instance = instance
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoadItem

        if (annotation != other.annotation) return false
        if (type != other.type) return false
        if (instance != other.instance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = annotation.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + instance.hashCode()
        return result
    }


}
package com.IceCreamQAQ.Yu.di

interface DataWriter<T> {

    operator fun invoke(instance: T, context: YuContext)

}



class ReflectFieldWriter<T>() : DataWriter<T> {
    override fun invoke(instance: T, context: YuContext) {
        TODO("Not yet implemented")
    }
}
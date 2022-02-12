package com.IceCreamQAQ.Yu.di

interface DataReader<T> {

    val type: Class<T>
    val name: String
    operator fun invoke(context: YuContext): T?

}
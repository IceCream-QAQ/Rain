package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.util.type.RelType
import java.lang.reflect.Type

abstract class DataReaderFactory(val context: ContextImpl, val type: RelType<*>) {

    open val childDataReader = ArrayList<DataReaderFactory>()

    open fun getChildReader(type: RelType<*>): DataReaderFactory? =
        childDataReader.firstOrNull { it.type.isAssignableFrom(type) }

    open operator fun <T> invoke(type: RelType<T>): DataReader<T> =
        getChildReader(type)?.invoke(type) ?: localDataReader(type)

    abstract fun <T> localDataReader(type: RelType<T>): DataReader<T>
    fun register(factory: DataReaderFactory) {
        childDataReader.add(factory)
    }

//    inline operator fun <reified T> invoke(): DataReader<T> = invoke(T::class.java)

}

open class ObjectDataReaderFactory(context: ContextImpl, type: RelType<*>) : DataReaderFactory(context, type) {

    override fun <T> localDataReader(type: RelType<T>): DataReader<T> = context.findContext(type.realClass)!!

}

//open class ListDataReaderFactory(context: ContextImpl, type: RelType<*>) : DataReaderFactory(context, type) {
//
//}
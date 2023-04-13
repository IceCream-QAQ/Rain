package com.IceCreamQAQ.Yu.di.impl

import com.IceCreamQAQ.Yu.di.ClassContext
import com.IceCreamQAQ.Yu.di.DataReader
import com.IceCreamQAQ.Yu.di.din
import com.IceCreamQAQ.Yu.di.impl.ContextImpl
import com.IceCreamQAQ.Yu.util.type.RelType

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

    override fun <T> localDataReader(type: RelType<T>): DataReader<T> = context.findContext(type.realClass)

}

open class ListDataReaderFactory(context: ContextImpl, type: RelType<*>) : DataReaderFactory(context, type) {
    class ArrayDataReader<T>(val context: ClassContext<T>) : DataReader<List<T>> {
        override fun invoke(): List<T>? {
            val instanceList = ArrayList<T>()

            if (context is LocalInstanceClassContext) {
                instanceList.add(context.instance)
            }

            if (context is BindableClassContext) {
                context.bindMap.forEach { (_, v) ->
                    v.getBean()?.let { instanceList.add(it) }
                }
            }

            if (context is InstanceAbleClassContext) {
                context.instanceMap.forEach { (_, v) ->
                    instanceList.add(v)
                }
                context.defaultInstance?.let { instanceList.add(it) }
            }

            return instanceList
        }

        override fun invoke(name: String): List<T>? = invoke()
    }

    override fun <T> localDataReader(type: RelType<T>): DataReader<T> {
        return ArrayDataReader(context.findContext(type.generics!![0].realClass)) as DataReader<T>
    }

}

open class MapDataReaderFactory(context: ContextImpl, type: RelType<*>) : DataReaderFactory(context, type) {
    class MapDataReader<T>(val context: ClassContext<T>) : DataReader<Map<String, T>> {
        override fun invoke(): HashMap<String, T>? {
            val instanceList = HashMap<String, T>()
            if (context is BindableClassContext) {
                context.bindMap.forEach { (k, v) ->
                    v.getBean()?.let { instanceList[k] = it }
                }
            }

            if (context is InstanceAbleClassContext) {
                context.instanceMap.forEach { (k, v) ->
                    instanceList[k] = v
                }
                context.defaultInstance?.let { instanceList[din] = it }
            }

            if (context is LocalInstanceClassContext) {
                instanceList[din] = context.instance
            }

            return instanceList
        }

        override fun invoke(name: String): HashMap<String, T>? = invoke()
    }

    override fun <T> localDataReader(type: RelType<T>): DataReader<T> {
        return MapDataReader(context.findContext(type.generics!![1].realClass)) as DataReader<T>
    }

}
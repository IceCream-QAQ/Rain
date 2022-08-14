package com.IceCreamQAQ.Yu.di.impl

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.arrayMap
import com.IceCreamQAQ.Yu.di.BeanCreator
import com.IceCreamQAQ.Yu.di.din
import com.IceCreamQAQ.Yu.named
import java.lang.reflect.Constructor

class NoPublicConstructorBeanCreator<T>(val clazz: Class<T>) : BeanCreator<T> {
    override fun invoke(): Nothing = error("类 ${clazz.name} 没有任何公开的构造函数！")
}

open class DefaultConstructorBeanCreator<T>(val constructor: Constructor<T>) : BeanCreator<T> {
    override fun invoke(): T =
        constructor.newInstance()
}

open class InjectConstructorBeanCreator<T>(
    val context: ContextImpl,
    val constructor: Constructor<T>
) : BeanCreator<T> {

    open val readers = constructor.parameters.map {
        context.run { it.annotation<Config>()?.run { getConfigReader(it.type) } ?: getDataReader(it.type) }
            .let { reader ->
                { it.named.let { named -> if (named == din) reader() else reader(it.named) } }
            }
    }

    override fun invoke(): T =
        constructor.newInstance(*readers.arrayMap { it() })

}
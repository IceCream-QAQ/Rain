package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.*
import com.IceCreamQAQ.Yu.di.impl.ContextImpl
import javax.inject.Inject
import javax.inject.Named

interface BeanInjector<T> {
    operator fun invoke(bean: T): T
}



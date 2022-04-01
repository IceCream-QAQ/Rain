package com.IceCreamQAQ.Yu.di

interface BeanInjector<T> {
    operator fun invoke(bean: T): T
}
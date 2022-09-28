package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.annotation
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.arrayMap
import com.IceCreamQAQ.Yu.named
import java.lang.reflect.Constructor

interface BeanCreator<T> {

    operator fun invoke(): T

}
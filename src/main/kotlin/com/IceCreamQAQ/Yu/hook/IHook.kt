package com.IceCreamQAQ.Yu.hook

import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer

interface IHook : ClassTransformer {

    val superHook: IHook?

    fun registerHook(item: IHookItem)

    fun findHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo?

    fun createInstanceHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo?
}
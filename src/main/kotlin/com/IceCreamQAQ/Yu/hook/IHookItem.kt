package com.IceCreamQAQ.Yu.hook

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

interface IHookItem {
    val hookRunnableInfo: HookRunnableInfo
    fun checkClass(clazz: String, node: ClassNode): Boolean
    fun checkMethod(method: String, descriptor: String, node: MethodNode): Boolean
}

class NoMatchHookItem @JvmOverloads constructor(
    override val hookRunnableInfo: HookRunnableInfo,
    private val clazz: String,
    private val method: String,
    private val descriptor: String? = null,
) : IHookItem {

    override fun checkClass(clazz: String, node: ClassNode): Boolean =
        clazz == this.clazz

    override fun checkMethod(method: String, descriptor: String, node: MethodNode): Boolean =
        method == clazz && this.descriptor?.let { descriptor == it } ?: true

}
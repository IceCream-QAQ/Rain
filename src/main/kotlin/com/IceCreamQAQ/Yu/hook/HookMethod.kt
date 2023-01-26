package com.IceCreamQAQ.Yu.hook

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

data class HookMethod(
    val clazz: ClassNode,
    val method: MethodNode,
    val identifier: String,
    val standardHooks: List<HookRunnableInfo>,
    val instanceHooks: List<HookRunnableInfo>
) {
    val isInstanceMode: Boolean
        inline get() = instanceHooks.isNotEmpty()

    val name: String = method.name

    val changeToName: String = "${name}_YuHookV2_OldMethod_$identifier"

    val descriptor: String = method.desc
}

//interface HookMethodContext


//interface HookMethod {
//    fun writeHook()
//}
//
//interface StandardHook : HookMethod {
//
//    fun MethodVisitor.writeStandardInitMethod()
//
//}
//
//interface InstanceHook : HookMethod {
//
//    fun MethodVisitor.writeInstanceInitMethod()
//
//}
//
//
//class OnlyStandardHookMethod(
//    val clazz: ClassNode,
//    val method: MethodNode,
//    val identifier: String,
//    val standardHooks: List<HookRunnableInfo>
//) : StandardHook {
//    override fun writeHook() {
//        TODO("Not yet implemented")
//    }
//
//    override fun MethodVisitor.writeStandardInitMethod() {
//        TODO("Not yet implemented")
//    }
//}
//
//class OnlyInstanceHookMethod(
//    val clazz: ClassNode,
//    val method: MethodNode,
//    val identifier: String,
//    val instanceHooks: List<HookRunnableInfo>
//) : InstanceHook {
//    override fun writeHook() {
//        TODO("Not yet implemented")
//    }
//
//    override fun MethodVisitor.writeInstanceInitMethod() {
//        TODO("Not yet implemented")
//    }
//}
//
//class StandardAndInstanceHookMethod(
//    val clazz: ClassNode,
//    val method: MethodNode,
//    val identifier: String,
//    val standardHooks: List<HookRunnableInfo>,
//    val instanceHooks: List<HookRunnableInfo>
//) : StandardHook, InstanceHook {
//    override fun writeHook() {
//        TODO("Not yet implemented")
//    }
//
//    override fun MethodVisitor.writeStandardInitMethod() {
//        TODO("Not yet implemented")
//    }
//
//    override fun MethodVisitor.writeInstanceInitMethod() {
//        TODO("Not yet implemented")
//    }
//}
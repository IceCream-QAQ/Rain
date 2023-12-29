package rain.hook

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

interface IHookItem {
    val hookRunnableInfo: HookRunnableInfo
    val checkClass: IClassChecker
    val checkMethod: IMethodChecker
}

fun interface IClassChecker {
    operator fun invoke(clazz: String, node: ClassNode): Boolean
}

fun interface IMethodChecker {
    operator fun invoke(method: String, descriptor: String, node: MethodNode): Boolean
}

open class ValHookItem(
    override val hookRunnableInfo: HookRunnableInfo,
    override val checkClass: IClassChecker,
    override val checkMethod: IMethodChecker
) : IHookItem

class FullMatchHookItem(
    val className: String,
    val methodName: String,
    val descriptor: String?,
    runnable: HookRunnableInfo
) : ValHookItem(
    runnable,
    IClassChecker { clazz, node -> clazz == className},
    IMethodChecker { method, desc, node ->
        (method == methodName) && descriptor?.let { it == desc } ?: true
    }
)
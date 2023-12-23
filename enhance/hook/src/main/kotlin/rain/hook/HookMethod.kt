package rain.hook

data class HookMethod(
    val clazz: String,
    val name: String,
    val descriptor: String,
    val identifier: String,
    val standardHooks: List<HookRunnableInfo>,
    val instanceHooks: List<HookRunnableInfo>
) {
    val isInstanceMode: Boolean
        inline get() = instanceHooks.isNotEmpty()

    val changeToName: String = "${name}_YuHookV2_OldMethod_$identifier"

    val paramDesc: String = descriptor.split(")")[0].substring(1)

}
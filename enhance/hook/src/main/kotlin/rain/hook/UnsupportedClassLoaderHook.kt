package rain.hook

class UnsupportedClassLoaderHook : IHook {

    override val superHook: IHook? = null

    val hookItems = ArrayList<HookItem>()

    override fun registerHook(item: HookItem) {
        hookItems.add(item)
    }

    override fun findHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo {
        error("[YuHook] YuHook 暂不支持 ClassLoader: ${this::class.java.classLoader::class.java.name}。")
    }

    override fun createInstanceHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo {
        error("[YuHook] YuHook 暂不支持 ClassLoader: ${this::class.java.classLoader::class.java.name}。")
    }

}
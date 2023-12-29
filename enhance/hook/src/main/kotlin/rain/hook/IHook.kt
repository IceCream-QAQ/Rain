package rain.hook

interface IHook {

    val superHook: IHook?

    fun registerHook(item: HookItem)

    fun findHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo

    fun createInstanceHookInfo(
        clazz: Class<*>,
        methodName: String,
        sourceMethodName: String,
        methodParas: Array<Class<*>>
    ): HookInfo
}
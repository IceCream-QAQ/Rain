package rain.di

interface ClassContext<T> : DataReader<T> {

    val clazz: Class<T>
    val name: String
        get() = clazz.name

    val multi: Boolean
    val instanceAble: Boolean
    val bindAble: Boolean

    val creator: BeanCreator<T>
    val injector: BeanInjector<T>

    operator fun get(name: String): T? = getBean(name)
    operator fun set(name: String, instance: T): T = putBean(name, instance)

    fun newBean(): T
    fun getBean(): T?
    fun getBean(name: String = YuContext.defaultInstanceName): T?
    fun putBean(name: String = YuContext.defaultInstanceName, instance: T): T

    fun putBinds(name: String, cc: ClassContext<out T>)

    override fun invoke(): T? = getBean()
    override fun invoke(name: String): T? = getBean(name)

}
package rain.di.kotlin

import rain.api.di.DiContext
import rain.di.YuContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface YuContextKotlinInjectBase {
    fun getContext_Rain_YuContext_Kotlin_ByInject(): YuContext
}

class YuContextKotlinInjectReadWriteProperty<T>(val name: String, val type: Class<*>) : ReadWriteProperty<Any, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value == null) {
            value = (thisRef as YuContextKotlinInjectBase)
                .getContext_Rain_YuContext_Kotlin_ByInject()
                .getBean(type, name) as? T?
        }
        return value!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

class YuContextKotlinConfigInjectReadWriteProperty<T>(val name: String, val type: Class<*>) :
    ReadWriteProperty<Any, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value == null) {
            value = (thisRef as YuContextKotlinInjectBase)
                .getContext_Rain_YuContext_Kotlin_ByInject()
                .configManager
                .getConfig(name, type) as? T?
        }
        return value!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

inline fun <reified T> inject(name: String = DiContext.defaultInstanceName): ReadWriteProperty<Any, T> =
    YuContextKotlinInjectReadWriteProperty(name, T::class.java)

inline fun <reified T> config(name: String = DiContext.defaultInstanceName): ReadWriteProperty<Any, T> =
    YuContextKotlinConfigInjectReadWriteProperty(name, T::class.java)
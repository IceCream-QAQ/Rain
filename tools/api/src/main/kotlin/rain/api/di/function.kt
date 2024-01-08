package rain.api.di

import rain.function.annotation
import java.lang.reflect.AnnotatedElement
import javax.inject.Named

internal const val din = DiContext.defaultInstanceName

inline fun <reified T> DiContext.getBean(name: String = DiContext.defaultInstanceName, nullFun: () -> T): T {
    return getBean(T::class.java, name) ?: nullFun()
}

val AnnotatedElement.named get() = annotation<Named>()?.value ?: din
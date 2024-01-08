package rain.api.annotation

import rain.api.loader.Loader
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class AutoBind

annotation class LoadBy(val value: KClass<out Loader>, val mastBean: Boolean = true)

annotation class Nullable(val value: String = "")
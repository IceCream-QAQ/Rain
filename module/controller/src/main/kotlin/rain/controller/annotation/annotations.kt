package rain.controller.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Before(val weight: Int = 0, val except: Array<String> = [], val only: Array<String> = [])

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class After(val weight: Int = 0, val except: Array<String> = [], val only: Array<String> = [])

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Catch(
    val weight: Int = 0,
    val error: KClass<out Throwable>,
    val except: Array<String> = [],
    val only: Array<String> = []
)

annotation class Path(val value: String)
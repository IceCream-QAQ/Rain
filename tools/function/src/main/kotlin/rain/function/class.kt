package rain.function

import java.lang.reflect.Method
import java.lang.reflect.Modifier

val Class<*>.isBean get() = !(this.isInterface || Modifier.isAbstract(this.modifiers))

fun Class<*>.getMethodOrNull(name: String, vararg params: Class<*>): Method? {
    return try {
        getMethod(name, *params)
    } catch (_: NoSuchMethodException) {
        null
    }
}

fun Class<*>.getDeclaredMethodOrNull(name: String, vararg params: Class<*>): Method? {
    return try {
        getDeclaredMethod(name, *params)
    } catch (_: NoSuchMethodException) {
        null
    }
}
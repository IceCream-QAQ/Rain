package rain.function

import java.lang.reflect.Modifier

fun Class<*>.isBean() = !(this.isInterface || Modifier.isAbstract(this.modifiers))

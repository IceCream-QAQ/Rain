package rain.function

import java.lang.reflect.Modifier

val Class<*>.isBean get() = !(this.isInterface || Modifier.isAbstract(this.modifiers))

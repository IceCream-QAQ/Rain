package rain.function

inline fun <reified T> Any.cast(): T? = this as? T
inline fun <reified T> Any.castNotNull(): T = this as T
inline fun <reified T> Any.castOrError(body: () -> Throwable): T = if (this is T) this else throw body()
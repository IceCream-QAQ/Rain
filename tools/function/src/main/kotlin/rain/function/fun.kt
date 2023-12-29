package rain.function

import java.util.*

fun <T> T.sout() = this.apply { println(this) }
fun uuid() = UUID.randomUUID().toString()

inline fun <E, reified R> Collection<E>.arrayMap(body: (E) -> R): Array<R?> {
    val array = arrayOfNulls<R>(size)
    forEachIndexed { i, it -> array[i] = body(it) }
    return array
}

inline fun <E, reified R> Array<out E>.arrayMap(body: (E) -> R): Array<R> {
    val array = arrayOfNulls<R>(size)
    forEachIndexed { i, it -> array[i] = body(it) }
    return array as Array<R>
}

inline fun <T, R> Iterable<T>.mutableMap(transform: (T) -> R): MutableList<R> {
    return mapTo(ArrayList(if (this is Collection<*>) this.size else 10), transform)
}

inline fun <K, V, RK, RV> Map<out K, V>.mapMap(transform: (Map.Entry<K, V>) -> Pair<RK, RV>): Map<RK, RV> {
    return mapOf(mapTo(ArrayList(size), transform))
}

inline fun <T, K, V> Iterable<T>.mapMap(transform: (T) -> Pair<K, V>): Map<K, V> {
    return mapOf(mapTo(ArrayList(if (this is Collection<*>) this.size else 10), transform))
}

fun <K, V> mapOf(i: Iterable<Pair<K, V>>): Map<K, V> =
    HashMap<K, V>(if (i is Collection<*>) i.size else 10).apply { i.forEach { put(it.first, it.second) } }
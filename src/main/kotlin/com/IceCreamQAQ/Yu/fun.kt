package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.di.din
import com.IceCreamQAQ.Yu.util.YuParaValueException
import com.alibaba.fastjson.JSON
import okhttp3.internal.and
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Named


fun Class<*>.isBean() = !(this.isInterface || Modifier.isAbstract(this.modifiers))

inline fun <reified T> String.toObject(): T = this.toObject(T::class.java)
fun <T> String.toObject(clazz: Class<T>) = JSON.parseObject(this, clazz)
fun String.toJSONObject() = JSON.parseObject(this)
fun Any.toJSONString() = JSON.toJSONString(this)

fun paraError(text: String) = YuParaValueException(text)

fun <K, V> Map<K, V>.mapOf(body: (K, V) -> Pair<K, V>?): HashMap<K, V> =
    HashMap<K, V>().also { this.forEach { (k, v) -> body(k, v)?.let { (nk, nv) -> it[nk] = nv } } }

val Throwable.stackTraceString: String
    get() {
        val sb = StringBuilder(this::class.java.name).append(": ").append(message).append("\n")
        getStackTraceString(sb)
        return sb.toString()
    }

private fun Throwable.getStackTraceString(sb: StringBuilder) {
    for (element in stackTrace) {
        sb.append("    at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})\n")
    }
    cause?.let {
        sb.append("Caused by: ").append(it::class.java.name).append(": ").append(it.message).append("\n")
        it.getStackTraceString(sb)
    }
}

fun String.toUpperCaseFirstOne(): String {
    return if (Character.isUpperCase(this[0])) this
    else (StringBuilder()).append(Character.toUpperCase(this[0])).append(this.substring(1)).toString();
}

fun String.toLowerCaseFirstOne(): String {
    return if (Character.isLowerCase(this[0])) this
    else (StringBuilder()).append(Character.toLowerCase(this[0])).append(this.substring(1)).toString();
}

inline fun <reified T : Annotation> Method.annotation(body: T.() -> Unit) = getAnnotation(T::class.java)?.apply(body)

val Method.fullName: String
    get() {
        val nameBuilder = StringBuilder(this.declaringClass.name).append(".").append(this.name).append("(")
        val max = parameterCount - 1
        for ((i, clazz) in parameterTypes.withIndex()) {
            nameBuilder.append(clazz.simpleName)
            if (i < max) nameBuilder.append(", ")
        }
        nameBuilder.append(")")
        return nameBuilder.toString()
    }

fun String.md5(charset: Charset = Charsets.UTF_8) = this.toByteArray(charset).md5
val String.md5: String get() = this.toByteArray().md5

val ByteArray.md5: String
    get() {
        val md5: MessageDigest
        md5 = try {
            MessageDigest.getInstance("MD5")
        } catch (var6: NoSuchAlgorithmException) {
            throw RuntimeException(var6)
        }
        val md5Bytes = md5.digest(this)
        val hexValue = java.lang.StringBuilder()
        for (i in md5Bytes.indices) {
            val value: Int = md5Bytes[i] and 255
            if (value < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(value))
        }
        return hexValue.toString()
    }

val Member.isExecutable get() = !isStatic && !isAbstract
val Member.isAbstract get() = Modifier.isAbstract(modifiers)
val Member.isStatic get() = Modifier.isStatic(modifiers)
val Member.isFinal get() = Modifier.isFinal(modifiers)

inline fun <reified T : Annotation> AnnotatedElement.hasAnnotation(): Boolean =
    getAnnotation(T::class.java)?.let { true } ?: false

inline fun <reified T : Annotation> AnnotatedElement.annotation(): T? = getAnnotation(T::class.java)
inline fun <reified T : Annotation> AnnotatedElement.annotation(body: T.() -> Unit): T? =
    getAnnotation(T::class.java)?.apply(body)

val AnnotatedElement.named get() = annotation<Named>()?.value ?: din

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
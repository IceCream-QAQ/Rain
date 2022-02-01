package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.util.YuParaValueException
import com.alibaba.fastjson.JSON
import okhttp3.internal.and
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


fun Class<*>.isBean() = !(this.isInterface || Modifier.isAbstract(this.modifiers))

inline fun <reified T> String.toObject(): T = this.toObject(T::class.java)
fun <T> String.toObject(clazz: Class<T>) = JSON.parseObject(this, clazz)
fun String.toJSONObject() = JSON.parseObject(this)
fun Any.toJSONString() = JSON.toJSONString(this)

fun paraError(text: String) = YuParaValueException(text)

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

inline fun <reified T : Annotation> Method.annotation(body: T.() -> Unit) = getAnnotation(T::class.java).apply(body)

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
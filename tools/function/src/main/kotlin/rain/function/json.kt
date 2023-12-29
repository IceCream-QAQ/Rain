package rain.function

import com.alibaba.fastjson2.JSON

inline fun <reified T> String.toObject(): T = this.toObject(T::class.java)
fun <T> String.toObject(clazz: Class<T>) = JSON.parseObject(this, clazz)
fun String.toJSONObject() = JSON.parseObject(this)
fun Any.toJSONString() = JSON.toJSONString(this)
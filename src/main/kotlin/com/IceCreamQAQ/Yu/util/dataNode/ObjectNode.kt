package com.IceCreamQAQ.Yu.util.dataNode

import com.IceCreamQAQ.Yu.*
import com.IceCreamQAQ.Yu.util.getOrPut
import com.IceCreamQAQ.Yu.util.type.RelType
import java.lang.reflect.Type

open class ObjectNode : BaseNode(), MutableIterable<Map.Entry<String, DataNode>> {

    protected open val dataMap: MutableMap<String, DataNode> = HashMap()

    open operator fun get(key: String): DataNode? = dataMap[key]
    open operator fun set(key: String, value: DataNode) {
        dataMap[key] = value
    }

    open fun remove(key: String): DataNode? = dataMap.remove(key)
    open fun has(key: String): Boolean = dataMap.containsKey(key)

    open fun putAll(other: ObjectNode) {
        other.forEach { (k, v) ->
            dataMap[k].let {
                if (it is ObjectNode && v is ObjectNode) it.putAll(v)
                else dataMap[k] = v
            }
        }
    }

    open fun merge(other: ObjectNode) {
        other.forEach { (k, v) ->
            dataMap[k].let {
                if (it is ObjectNode && v is ObjectNode) it.merge(v)
                else if (it is ArrayNode && v is ArrayNode) it.addAll(v)
                else if (it is ArrayNode) it.add(v)
                else if (v is ArrayNode && it != null) dataMap[k] = v.apply { addFirst(it) }
                else if (it != null) dataMap[k] = ArrayNode(it, v)
                else dataMap[k] = v
            }
        }
    }

    open fun removeAll() {
        dataMap.clear()
    }

    open fun getOrPut(key: String, defaultValue: DataNode) = dataMap.getOrPut(key, defaultValue)
    open fun getOrPut(key: String, defaultValue: () -> DataNode) = dataMap.getOrPut(key, defaultValue)
    open fun getOrDefault(key: String, defaultValue: DataNode) = dataMap.getOrDefault(key, defaultValue)
    open fun getOrDefault(key: String, defaultValue: () -> DataNode) = dataMap.getOrDefault(key, defaultValue)

    open fun containsKey(key: String): Boolean = dataMap.containsKey(key)

    open fun size(): Int = dataMap.size

    override fun iterator() = dataMap.iterator()


    override fun asBoolean(): Boolean = error("无法将一个 Object 数据节点转化为 Boolean！")

    override fun asByte(): Byte = error("无法将一个 Object 数据节点转化为 Byte！")

    override fun asShort(): Short = error("无法将一个 Object 数据节点转化为 Short！")

    override fun asInt(): Int = error("无法将一个 Object 数据节点转化为 Int！")

    override fun asLong(): Long = error("无法将一个 Object 数据节点转化为 Long！")

    override fun asFloat(): Float = error("无法将一个 Object 数据节点转化为 Float！")

    override fun asDouble(): Double = error("无法将一个 Object 数据节点转化为 Double！")

    override fun asChar(): Char = error("无法将一个 Object 数据节点转化为 Char！")

    override fun asString(): String = error("无法将一个 Object 数据节点转化为 String！")

    override fun <T> asEnum(enumClass: RelType<T>): T {
        TODO()
    }

    override fun <T> castTo(type: RelType<T>): T {
        val clazz = type.realClass
        if (clazz == this::class.java) return this as T
        val defaultConstructor = kotlin.runCatching { clazz.getConstructor() }.getOrNull()

        val instance = defaultConstructor?.newInstance() ?: error("类: ${clazz.name} 未包含公开的无参构造！")
        val writeList = HashMap<String, Pair<Type, (Any?) -> Unit>>()

        clazz.declaredFields
            .asSequence()
            .filter { !it.isStatic }
            .filter { !it.isFinal }
            .forEach {
                writeList[it.name] = it.genericType to { v ->
                    it.isAccessible = true
                    it.set(instance, v)
                }
            }

        clazz.methods
            .asSequence()
            .filter { it.isExecutable }
            .filter { it.parameters.size == 1 }
            .filter { it.name.startsWith("set") }
//            .filter { it.hasAnnotation<Inject>() }
            .forEach {
                val name = it.name.substring(3)
                writeList[name] = it.parameters[0].parameterizedType to { v -> it.invoke(instance, v) }
            }

        writeList.forEach { (name, v) ->
            get(name)?.asObject(RelType.create(v.first))?.let { v.second.invoke(it) }
        }

        return instance
    }


    override fun <T> asArray(type: RelType<T>): List<T> {
        return arrayListOf(asObject(type))
    }
}
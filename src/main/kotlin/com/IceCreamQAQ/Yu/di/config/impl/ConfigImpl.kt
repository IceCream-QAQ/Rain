package com.IceCreamQAQ.Yu.di.config.impl

import com.IceCreamQAQ.Yu.di.config.ConfigManager
import com.IceCreamQAQ.Yu.di.config.ConfigReader
import com.IceCreamQAQ.Yu.util.dataNode.ArrayNode
import com.IceCreamQAQ.Yu.util.dataNode.ObjectNode
import com.IceCreamQAQ.Yu.util.dataNode.StringNode
import com.IceCreamQAQ.Yu.util.getOrPut
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import kotlin.collections.HashMap

private typealias ModeFileMap = MutableMap<String, MutableMap<String, ObjectNode>>

open class ConfigImpl(
    val classLoader: ClassLoader,
    var runMode: String?,
    val launchPackage: String?
) : ConfigManager {

    companion object {
        private val log = LoggerFactory.getLogger(ConfigImpl::class.java)
    }


    protected open val rootNode = ObjectNode()

    fun init(): ConfigImpl {
        log.info("[配置管理器] 初始化.")

        val modeFileMap = HashMap<String, MutableMap<String, ObjectNode>>()
        loadByMode(modeFileMap, "module", "conf/module")
        loadByMode(modeFileMap, "conf", "conf")

        runMode = runMode ?: "dev"
        log.info("[配置管理器] 加载模式: $runMode.")
        loadByMode(modeFileMap, runMode!!, "conf/$runMode")

        loadByMode(modeFileMap, "runLocation", "conf")

        mergeMode(modeFileMap)
        log.info("[配置管理器] 初始化完成.")

        return this
    }

    /*** 配置加载规则。
     * 同文件下，同名合并为 Array。
     * 不同模式，同文件名下，同名覆盖。
     * 不同模式，不同文件，同名合并为 Array。
     */
    protected open fun mergeMode(modeFileMap: ModeFileMap) {
        val fileModeMap = HashMap<String, MutableMap<String, ObjectNode>>()

        modeFileMap.filter { it.key != "module" }
            .forEach { (mode, files) ->
                files.forEach { (file, node) ->
                    fileModeMap.getOrPut(file, HashMap())[mode] = node
                }
            }

        val fileNodeMap = HashMap<String, ObjectNode>()

        fileModeMap.forEach { (file, nodeMap) ->
            fileNodeMap[file] = nodeMap.getOrDefault("conf", ObjectNode())
                .apply {
                    putAll(nodeMap.getOrDefault(runMode, ObjectNode()))
                    putAll(nodeMap.getOrDefault("runLocation", ObjectNode()))
                }
        }

        modeFileMap["module"]?.values?.forEach { rootNode.merge(it) }
        fileNodeMap.values.forEach { rootNode.merge(it) }
    }

    protected open fun loadByMode(modeFileMap: ModeFileMap, mode: String, path: String) {
        modeFileMap.getOrPut(mode, HashMap()).let {
            if (mode == "runLocation") loadByFolder(it, path)
            else loadByResource(it, path)
        }
    }

    protected open fun loadByResource(fileMap: MutableMap<String, ObjectNode>, path: String) {
        val dirs = classLoader.getResources(path)!!
        for (url: URL in dirs) {
            val protocol = url.protocol
            if ("file" == protocol) loadByFolder(fileMap, URLDecoder.decode(url.file, "UTF-8"))
            else if ("jar" == protocol) {
                val prefix = "$path/"
                val jar = (url.openConnection() as JarURLConnection).jarFile
                for (entry in jar.entries()) {
                    if (entry.isDirectory) continue
                    val name = entry.name.replace(prefix, "")
                    if (name.contains("/")) continue
                    loadConfigFile(name, jar.getInputStream(entry), fileMap.getOrPut(name, ObjectNode()))
                }
            }
        }
    }

    protected open fun loadByFolder(fileMap: MutableMap<String, ObjectNode>, path: String) {
        File(path).apply {
            if (isDirectory)
                listFiles()?.forEach {
                    if (it.isFile) it.name.let { name ->
                        loadConfigFile(name, FileInputStream(it), fileMap.getOrPut(name, ObjectNode()))
                    }
                }
        }
    }

    protected open val forceArrayPropertiesName =
        arrayOf(
            "yu.scanPackages",
            "yu.classRegister",
            "yu.modules",
        )

    protected open fun loadConfigFile(name: String, inputStream: InputStream, node: ObjectNode) {
        log.debug("[配置管理器] 加载配置文件: $name。")

        when {
            name.endsWith(".properties") -> loadConfigByProperties(node, inputStream)
//            name.endsWith(".json") -> loadConfigByJSON(jo, inputStream)
//            name.endsWith(".yml") || name.endsWith(".yaml") -> loadConfigByYaml(jo, inputStream)
        }
    }

    protected open fun loadConfigByProperties(node: ObjectNode, inputStream: InputStream) {
        val properties = Properties()
        properties.load(InputStreamReader(inputStream, "UTF-8"))

        for (name in properties.keys) {
            name as String
            val value = properties.getProperty(name)!!

            val valueNode =
                if ((value.startsWith("[") && value.endsWith("]") && !value.endsWith("\\]")))
                    ArrayNode().apply {
                        val cs = StringBuilder()
                        var i = 1
                        val max = value.length - 1
                        fun next(): Char = value[++i]
                        while (i < max) {
                            value[i].let {
                                if (it != ',') cs.append(if (it == '\\') next() else it)
                                else add(StringNode(cs.toString().trim().apply { cs.clear() }))
                            }
                            i++
                        }
                        add(StringNode(cs.toString().trim().apply { cs.clear() }))
                    }
                else if (name in forceArrayPropertiesName || name.endsWith("["))
                    ArrayNode(StringNode(value))
                else StringNode(value)

            val nameNodes = name.split(".")
            var o = node
            for (i in 0 until nameNodes.size - 1) {
                val nodeName = nameNodes[i]
                o = o.getOrPut(nodeName, ObjectNode()).let {
                    when (it) {
                        is ObjectNode -> it
                        is ArrayNode -> ObjectNode().apply { it.add(this) }
                        else -> ObjectNode().apply { o[nodeName] = this }
                    }
                }
            }

            val lastName = nameNodes.last()
            val last = o[lastName]
            if (last == null) o[lastName] = valueNode
            else if (last is ArrayNode) if (valueNode is ArrayNode) last.addAll(valueNode) else last.add(valueNode)
            else ArrayNode(last)
                .apply { if (valueNode is ArrayNode) addAll(valueNode) else add(valueNode) }
                .apply { o[lastName] = this }
        }
    }

    override fun <T> getConfigReader(type: Type): ConfigReader<T> {
        TODO("Not yet implemented")
    }

    override fun <T> getConfigWriter(type: Type): ConfigReader<T> {
        TODO("Not yet implemented")
    }
}


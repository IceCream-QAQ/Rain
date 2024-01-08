package rain.di.config.impl

import rain.di.config.ConfigManager
import rain.di.config.ConfigReader
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import rain.function.dataNode.ObjectNode
import rain.function.dataNode.ArrayNode
import rain.function.dataNode.DataNode
import rain.function.dataNode.StringNode
import rain.function.type.RelType
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import kotlin.collections.HashMap

private typealias ModeFileMap = MutableMap<String, MutableMap<String, ObjectNode>>

open class ConfigImpl(val classLoader: ClassLoader, var runMode: String?, val launchPackage: String?) : ConfigManager {

    companion object {
        private val log = LoggerFactory.getLogger(ConfigImpl::class.java)
    }

    protected open val rootNode = ObjectNode()

    fun init(): ConfigImpl {
        log.info("[配置管理器] 初始化。")

        val modeFileMap = HashMap<String, MutableMap<String, ObjectNode>>()
        loadByMode(modeFileMap, "module", "conf/module")
        loadByMode(modeFileMap, "conf", "conf")

        runMode = runMode ?: "prod"
        log.info("[配置管理器] 加载模式: $runMode。")
        loadByMode(modeFileMap, runMode!!, "conf/$runMode")

        loadByMode(modeFileMap, "runLocation", "conf")

        mergeMode(modeFileMap)

        launchPackage?.let {
            ((rootNode.getOrPut("rain") { ObjectNode() } as ObjectNode)
                .getOrPut("scanPackages") { ArrayNode() } as ArrayNode).add(StringNode(it))
        }
        (rootNode.getOrPut("rain") { ObjectNode() } as ObjectNode)["runMode"] = StringNode(runMode!!)

        log.info("[配置管理器] 初始化完成。")

        return this
    }

    /*** 配置加载规则。
     * 同文件下，同名合并为 Array。
     * 不同模式，同文件名下，同名覆盖。
     * 不同模式，不同文件，同名合并为 Array。
     */
    protected open fun mergeMode(modeFileMap: ModeFileMap) {
        val fileModeMap = HashMap<String, MutableMap<String, ObjectNode>>()

        modeFileMap.filter { it.key != "module" }.forEach { (mode, files) ->
            files.forEach { (file, node) ->
                fileModeMap.getOrPut(file) { HashMap() }[mode] = node
            }
        }

        val fileNodeMap = HashMap<String, ObjectNode>()

        fileModeMap.forEach { (file, nodeMap) ->
            fileNodeMap[file] = nodeMap.getOrDefault("conf", ObjectNode()).apply {
                putAll(nodeMap.getOrDefault(runMode, ObjectNode()))
                putAll(nodeMap.getOrDefault("runLocation", ObjectNode()))
            }
        }

        modeFileMap["module"]?.values?.forEach { rootNode.merge(it) }
        fileNodeMap.values.forEach { rootNode.merge(it) }
    }

    protected open fun loadByMode(modeFileMap: ModeFileMap, mode: String, path: String) {
        modeFileMap.getOrPut(mode) { HashMap() }.let {
            if (mode == "runLocation") loadByFolder(it, mode, path)
            else loadByResource(it, mode, path)
        }
    }

    protected open fun loadByResource(fileMap: MutableMap<String, ObjectNode>, mode: String, path: String) {
        val dirs = classLoader.getResources(path)!!
        for (url: URL in dirs) {
            val protocol = url.protocol
            if ("file" == protocol) loadByFolder(fileMap, mode, URLDecoder.decode(url.file, "UTF-8"))
            else if ("jar" == protocol) {
                val prefix = "$path/"
                val jar = (url.openConnection() as JarURLConnection).jarFile
                for (entry in jar.entries()) {
                    if (entry.isDirectory) continue
                    val name = entry.name.replace(prefix, "")
                    if (name.contains("/")) continue
                    loadConfigFile(mode, name, jar.getInputStream(entry), fileMap.getOrPut(name) { ObjectNode() })
                }
            }
        }
    }

    protected open fun loadByFolder(fileMap: MutableMap<String, ObjectNode>, mode: String, path: String) {
        File(path).apply {
            if (isDirectory) listFiles()?.forEach {
                if (it.isFile) it.name.let { name ->
                    loadConfigFile(mode, name, FileInputStream(it), fileMap.getOrPut(name) { ObjectNode() })
                }
            }
        }
    }

    protected open val forceArrayPropertiesName = arrayOf(
        "rain.scanPackages",
        "rain.classRegisters",
        "rain.modules",
    )

    protected open fun loadConfigFile(mode: String, name: String, inputStream: InputStream, node: ObjectNode) {
        log.debug("[配置管理器] 加载配置文件, 模式: $mode, 文件: $name。")

        when {
            name.endsWith(".properties") -> loadConfigByProperties(node, inputStream)
            name.endsWith(".json") -> loadConfigByJSON(node, inputStream)
            name.endsWith(".yml") || name.endsWith(".yaml") -> loadConfigByYaml(node, inputStream)
        }
    }

    protected open fun loadConfigByProperties(node: ObjectNode, inputStream: InputStream) {
        val properties = Properties()
        properties.load(InputStreamReader(inputStream, "UTF-8"))

        for (name in properties.keys) {
            name as String
            val value = properties.getProperty(name)!!

            val valueNode =
                if ((value.startsWith("[") && value.endsWith("]") && !value.endsWith("\\]"))) ArrayNode().apply {
                    val cs = StringBuilder()
                    var i = 1
                    fun next(): Char = value[++i]
                    while (i < value.length - 1) {
                        value[i].let {
                            if (it != ',') cs.append(if (it == '\\') next() else it)
                            else add(StringNode(cs.toString().trim().apply { cs.clear() }))
                        }
                        i++
                    }
                    add(StringNode(cs.toString().trim().apply { cs.clear() }))
                }
                else if (name in forceArrayPropertiesName || name.endsWith("[")) ArrayNode(StringNode(value))
                else StringNode(value)

            val nameNodes = name.split(".")
            var o = node
            for (i in 0 until nameNodes.size - 1) {
                val nodeName = nameNodes[i]
                o = o.getOrPut(nodeName) { ObjectNode() }.let {
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
            else ArrayNode(last).apply { if (valueNode is ArrayNode) addAll(valueNode) else add(valueNode) }
                .apply { o[lastName] = this }
        }
    }

    private fun loadConfigByJSON(node: ObjectNode, inputStream: InputStream) {
        val jo = JSON.parseObject(inputStream)
        readJSONObject(node, jo)
    }

    private fun readJSONObject(node: ObjectNode, jo: JSONObject) {
        for ((name, value) in jo)
            when (value) {
                is JSONObject -> readJSONObject(node.getOrPut(name) { ObjectNode() } as ObjectNode, value)
                is JSONArray -> readJSONArray(node.getOrPut(name) { ArrayNode() } as ArrayNode, value)
                else -> node[name] = StringNode(value.toString())
            }
    }

    private fun readJSONArray(node: ArrayNode, ja: JSONArray) {
        for (value in ja)
            node.add(
                when (value) {
                    is JSONObject -> ObjectNode().also { readJSONObject(it, value) }
                    is JSONArray -> ArrayNode().also { readJSONArray(it, value) }
                    else -> StringNode(value.toString())
                }
            )
    }

    private fun loadConfigByYaml(node: ObjectNode, inputStream: InputStream) {
        val yaml = Yaml()
        val map = yaml.load(inputStream) as Map<String, Any>
        readYamlObject(node, map)
    }

    private fun readYamlObject(node: ObjectNode, map: Map<String, Any>) {
        for ((key, value) in map)
            when (value) {
                is Map<*, *> ->
                    readYamlObject(node.getOrPut(key) { ObjectNode() } as ObjectNode, value as Map<String, Any>)

                is List<*> -> readYamlArray(node.getOrPut(key) { ArrayNode() } as ArrayNode, value as List<Any>)
                else -> node[key] = StringNode(value.toString())
            }
    }

    private fun readYamlArray(node: ArrayNode, list: List<Any>) {
        for (value in list)
            node.add(
                when (value) {
                    is Map<*, *> -> ObjectNode().also { readYamlObject(it, value as Map<String, Any>) }
                    is List<*> -> ArrayNode().also { readYamlArray(it, value as List<Any>) }
                    else -> StringNode(value.toString())
                }
            )
    }

    protected open fun getConfigNode(name: String): DataNode? {
        var o: ObjectNode = rootNode
        val nodes = name.split(".")
        for (i in 0 until nodes.size - 1) {
            o = o[nodes[i]]?.let {
                when (it) {
                    is ObjectNode -> it
                    is ArrayNode -> it.firstOrNull { item -> item is ObjectNode } as ObjectNode
                    else -> null
                }
            } ?: return null
        }
        return o[nodes.last()]
    }

    override fun <T> getConfig(name: String, type: RelType<T>): T? {
        return getConfigNode(name)?.asObject(type)
    }

    override fun <T> getArray(name: String, type: RelType<T>): List<T> {
        return getConfigNode(name)?.asArray(type) ?: emptyList()
    }

    override fun <T> getMap(name: String, type: RelType<T>): Map<String, T> {
        return getConfigNode(name)?.asMap(type) ?: emptyMap()
    }

    override fun <T> getConfigReader(name: String, type: RelType<T>): ConfigReader<T> =
        if (type.realClass == List::class.java) ConfigArrayReader(this, name, type.generics!![0])
        else ConfigNodeReader(this, name, type)

    override fun <T> getConfigWriter(name: String, type: RelType<T>): ConfigReader<T> {
        TODO("Not yet implemented")
    }
}


package com.IceCreamQAQ.Yu.di

import com.IceCreamQAQ.Yu.AppLogger
import com.IceCreamQAQ.Yu.error.ConfigFormatError
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.slf4j.LoggerFactory
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import kotlin.collections.ArrayList

class ConfigManager(val classloader: ClassLoader, private val logger: AppLogger, runMode: String?) {

    private var config: JSONObject = JSONObject()

    private val log = LoggerFactory.getLogger(ConfigManager::class.java)

    init {

//        logger.logDebug("ConfigManager", "Init.")
        log.info("ConfigManager Init.")

        loadFolder("conf/module")
        loadFolder("conf")


        val mode = runMode ?: {
            var m: String? = null
            for (v in config.values) {
                m = get("yu.config.runMode", String::class.java, v as JSONObject)
                if (m != null) break
            }
            m ?: "dev"
        }()

        log.info("ConfigManager Config Mode: $mode.")
//        logger.logDebug("ConfigManager", "Config Mode: $mode")

        loadFolder("conf/$mode")

        val conf = File(File("").absoluteFile, "conf")
        if (conf.isDirectory) {
            loadUrl(conf.toURI().toURL(), "conf")
        }

        val c = JSONObject()
        for (key in config.keys) {
            appendObj(c, config[key] as JSONObject, true)
        }
        config = c

        log.info("ConfigManager Init Success.")
//        logger.logDebug("ConfigManager", "Init Success")

    }


    private fun loadFolder(folder: String): List<String> {

        val classloader = this.javaClass.classLoader
        val dirs: Enumeration<URL> = classloader.getResources(folder)!!

        val configFiles = ArrayList<String>();

        for (url in dirs) {

            loadUrl(url, folder)

        }

        return configFiles
    }

    private fun loadUrl(url: URL, folder: String) {
        val protocol = url.protocol
        if ("file" == protocol) {
            val filePath = URLDecoder.decode(url.file, "UTF-8")
            val dir = File(filePath)
            if (dir.isDirectory)
                for (ss in dir.listFiles()) {
                    if (ss.isFile) {
                        val name = ss.name
//                        configFiles.add(name)
                        loadConfigFile(name, FileInputStream(ss))
                    }
                }
        } else if ("jar" == protocol) {
            val prefix = "$folder/"
            val jar = (url.openConnection() as JarURLConnection).jarFile
            for (entry in jar.entries()) {
                if (entry.isDirectory) continue
                val name = entry.name.replace(prefix, "")
                if (name.contains("/")) continue
                loadConfigFile(name, jar.getInputStream(entry))
            }
        }
    }

    private fun loadConfigFile(name: String, inputStream: InputStream) {
        log.debug("ConfigManager LoadConfig: $name")
//        logger.logDebug("ConfigManager", "LoadConfig: $name")

        val jo = config[name] as JSONObject? ?: {
            val jo = JSONObject()
            config[name] = jo
            jo
        }()

        when {
            name.endsWith(".properties") -> loadConfigByProperties(jo, inputStream)
            name.endsWith(".json") -> loadConfigByJSON(jo, inputStream)
            name.endsWith(".yml") || name.endsWith(".yaml") -> loadConfigByYaml(jo, inputStream)
        }
    }


    private fun loadConfigByProperties(jo: JSONObject, inputStream: InputStream) {
        val prop = Properties()
        prop.load(inputStream)

        val o = JSONObject()
        for (oo in prop.keys) {
            val s = checkPropName(oo.toString())
            val ss = s.split(".")
            var ooo = o
            for (i in ss.indices) {
                val sss = ss[i]

                if (i == ss.lastIndex) {
                    if (sss.startsWith("[")) {
                        val ssss = sss.replace("[", "")
                        val oooo = ooo[ssss]
                        if (oooo is JSONArray) oooo.add(prop[oo])
                        else {
                            val ooooo = JSONArray()
                            if (oooo != null) ooooo.add(oooo)
                            ooooo.add(prop[oo])
                            ooo[ssss] = ooooo
                        }
                    } else ooo[sss] = prop[oo]
                    continue
                }

                if (sss.startsWith("[")) {
                    val index = sss.split("[").size - 2
                    val ssss = sss.substring(index + 1)
                    val oooo = ooo[ssss]
                    if (oooo is JSONArray) {
                        if (oooo.size <= index) {
                            val ooooo = JSONObject()
                            oooo.add(ooooo)
                            ooo = ooooo
                        } else {
                            var ooooo = oooo[index]
                            if (ooooo !is JSONObject) {
                                ooooo = JSONObject()
                                oooo[index] = ooooo
                            }
                            ooo = ooooo
                        }

                    } else {
                        val ooooo = JSONArray()
                        if (oooo != null) ooooo.add(oooo)
                        val oooooo = JSONObject()
                        ooooo.add(oooooo)
                        ooo[ssss] = ooooo
                        ooo = oooooo
                    }
                    continue
                }
                var oooo = ooo[sss]

                if (oooo == null || oooo !is JSONObject) {
                    oooo = JSONObject()
                }

                ooo[sss] = oooo
                ooo = oooo
            }
        }


        appendObj(jo as JSONObject, o)
    }

    private fun loadConfigByJSON(jo: JSONObject, inputStream: InputStream) {
        val sb = StringBuilder()

        val fr = BufferedReader(InputStreamReader(inputStream))
        var s = fr.readLine()
        while (s != null) {
            sb.append(s)
            s = fr.readLine()
        }

        val o = JSON.parseObject(sb.toString())

        appendObj(jo, o)
    }

    private fun loadConfigByYaml(jo: JSONObject, inputStream: InputStream) {
    }

    private fun appendObj(o: JSONObject, oo: JSONObject, append: Boolean = false) {
        for (s in oo.keys) {
            val v = o[s]
            val vv = oo[s]
            if (v == null) o[s] = vv
            else if (v is JSONObject && vv is JSONObject) appendObj(v, vv, append)
            else if (append && v is JSONArray && vv is JSONArray) v.addAll(vv)
            else o[s] = vv
        }
    }

    fun <T> get(key: String, type: Class<T>, jo: JSONObject = config): T? {
        var co: JSONObject? = jo
        val ns = key.split(".")
        val max = ns.size - 1
        for (i in 0..max) {
            val n = ns[i]

            if (n.endsWith("]")) {
                val nn = n.substring(0, n.length - 1).split("[")
                val oo = co?.get(nn[0]) ?: return null
                if (oo !is JSONArray) throw ConfigFormatError("Config Format Error: key $key is not a array!")

                val num = Integer.parseInt(nn[1])
                if (i == max) return oo.getObject(num, type)
                val ooo = oo.get(num)
                if (ooo !is JSONObject) throw ConfigFormatError("Config Format Error: key $key is not a object!")
                co = ooo
            } else {
                val oo = co?.get(n) ?: return null
                try {
                    if (i == max) {
                        if (oo is JSONObject) {
                            return oo.toJavaObject(type)
                        }
                        return oo as T
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw ConfigFormatError("Config Format Error: key $key's value not a ${type.name} object!")
                }
                if (oo !is JSONObject) throw ConfigFormatError("Config Format Error: key $key is not a object!")
                co = oo
            }

        }
        return co?.getObject(ns[max + 1], type)
    }

    fun <T> getArray(key: String, type: Class<T>): MutableList<T>? {
        val o = get(key, Any::class.java) ?: return null
        if (o is JSONArray) return o.toJavaList(type)
//        throw ConfigFormatError("Config Format Error: key $key is not a array!")
        val ja = JSONArray()
        ja.add(o)
        return ja.toJavaList(type)
    }

    private fun checkPropName(name: String): String {
        return when (name) {
            "yu.scanPackages" -> "yu.[scanPackages"
            "yu.classRegister" -> "yu.[classRegister"
            else -> name
        }
    }

//    fun <T> toArray(key: String, type: Class<T>):Array<T>?{
////        val jr = getArray(key, type)?:return null
//        val o = get(key, Any::class.java) ?: return null
//        if (o is JSONArray) return o.toJavaList(type)
////        throw ConfigFormatError("Config Format Error: key $key is not a array!")
//        val ja = JSONArray()
//        ja.add(o)
//        return ja.toArray(Array<T>())
//    }
}


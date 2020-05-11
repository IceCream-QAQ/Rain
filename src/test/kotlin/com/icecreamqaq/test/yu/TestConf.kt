package com.icecreamqaq.test.yu

import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*

class TestConf {

    fun run(){

        val classloader = this.javaClass.classLoader

        val dirs: Enumeration<URL> = classloader.getResources("com/alibaba/fastjson")!!

        for (url in dirs) {
            val protocol = url.protocol
            if ("file" == protocol){
                val filePath = URLDecoder.decode(url.file, "UTF-8")
                val dir = File(filePath)
                for (f in dir.listFiles()) {
                    println(f.name)
                }
            }else if ("jar" == protocol){
                val jar = (url.openConnection() as JarURLConnection).jarFile
                for (entry in jar.entries()) {
                    if (entry.isDirectory)
                    println(entry.name)

//                    jar.getInputStream()
                }
            }

        }

    }

}

fun main(){

    TestConf().run()

}
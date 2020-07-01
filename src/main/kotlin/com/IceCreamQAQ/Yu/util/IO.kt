package com.IceCreamQAQ.Yu.util

import com.IceCreamQAQ.Yu.annotation.NotSearch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

@NotSearch
class IO {

    @NotSearch
    companion object {

        @JvmStatic
        val tmpLocation = File("tmp")

        init {
            if (!tmpLocation.exists() || !tmpLocation.isDirectory) {
                tmpLocation.delete()
                tmpLocation.mkdirs()
            }
        }

        @JvmStatic
        fun read(inputStream: InputStream, close: Boolean = true): ByteArray {
            val bs = inputStream.readBytes()
            if (close) inputStream.close()
            return bs
        }

        @JvmStatic
        @JvmOverloads
        fun copy(inputStream: InputStream, outputStream: OutputStream, close: Boolean = true) {
            inputStream.copyTo(outputStream)
            if (close) {
                inputStream.close()
                outputStream.close()
            }
        }

        @JvmStatic
        fun tmpFile() = File(tmpLocation, UUID.randomUUID().toString())

        @JvmStatic
        fun writeTmpFile(fileName: String, byteArray: ByteArray) {
            writeFile(File(tmpLocation, fileName), byteArray)
        }

        @JvmStatic
        fun writeFile(file: File, byteArray: ByteArray) {
            val o = FileOutputStream(file)
            o.write(byteArray)
            o.close()
        }
    }
}
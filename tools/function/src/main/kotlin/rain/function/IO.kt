package rain.function

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class IO {

    companion object {

        @JvmStatic
        val tmpLocation = newFolder("tmp")

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
        @JvmOverloads
        fun writeFile(inputStream: InputStream, outFile: File = tmpFile(), append: Boolean = false, close: Boolean = true): File {
            val outputStream = FileOutputStream(outFile, append)
            inputStream.copyTo(outputStream)
            if (close) {
                inputStream.close()
                outputStream.close()
            }
            return outFile
        }

        @JvmStatic
        fun tmpFile() = File(tmpLocation, UUID.randomUUID().toString())

        @JvmStatic
        fun writeTmpFile(fileName: String, byteArray: ByteArray) {
            writeFile(File(tmpLocation, fileName), byteArray)
        }

        @JvmStatic
        @JvmOverloads
        fun writeFile(file: File, byteArray: ByteArray, append: Boolean = false) {
            val o = FileOutputStream(file, append)
            o.write(byteArray)
            o.close()
        }
    }
}
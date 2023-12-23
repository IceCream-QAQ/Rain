package rain.classloader

interface IRainClassLoader {

    fun define(name: String, data: ByteArray): Class<*>

    fun forName(name: String, initialize: Boolean): Class<*>

}
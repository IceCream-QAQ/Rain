package rain.classloader

class SpawnClassLoader(classLoader: ClassLoader) : ClassLoader(classLoader), IRainClassLoader {

    override fun define(name: String, data: ByteArray): Class<*> {
        return defineClass(name, data, 0, data.size)
    }


    override fun forName(name: String, initialize: Boolean): Class<*> {
        return loadClass(name, initialize)
    }
}

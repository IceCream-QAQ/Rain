package rain.di.config.impl

import rain.di.config.ConfigManager
import rain.di.config.ConfigReader
import rain.function.type.RelType

class ConfigArrayReader<T>(val config: ConfigManager, val name: String, val type: RelType<*>) : ConfigReader<T> {
    override fun invoke(): T = config.getArray(name, type) as T

    override fun invoke(name: String): T = config.getArray(name, type) as T
}
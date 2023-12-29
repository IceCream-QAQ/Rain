package rain.di.config.impl

import rain.di.config.ConfigManager
import rain.di.config.ConfigReader
import rain.function.type.RelType

class ConfigNodeReader<T>(val config: ConfigManager, val name: String, val type: RelType<T>) : ConfigReader<T> {
    override fun invoke(): T? = config.getConfig(name, type)

    override fun invoke(name: String): T? = config.getConfig(name, type)
}
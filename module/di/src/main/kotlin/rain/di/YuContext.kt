package rain.di

import rain.api.di.DiContext
import rain.di.config.ConfigManager

interface YuContext : DiContext {

    val configManager: ConfigManager

    fun registerClass(context: ClassContext<*>)

}



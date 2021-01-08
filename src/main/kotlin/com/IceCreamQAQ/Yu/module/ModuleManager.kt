package com.IceCreamQAQ.Yu.module

import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import java.lang.RuntimeException
import javax.inject.Inject

class ModuleManager {

    @Config("yu.modules")
    private var modules: List<String>? = null

    @Inject
    private lateinit var context: YuContext

    fun loadModule() {
        modules?.forEach {
            try {
                val moduleInstance = context.getBean(it) as? Module ?: error("Bean $it Not Found!")
                moduleInstance.onLoad()
            } catch (ex: Exception) {
                throw RuntimeException("Load Module $it Error!", ex)
            }
        }
    }

}
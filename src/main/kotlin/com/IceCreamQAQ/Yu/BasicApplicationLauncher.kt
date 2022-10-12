package com.IceCreamQAQ.Yu

import com.IceCreamQAQ.Yu.loader.AppClassloader
import com.IceCreamQAQ.Yu.loader.IRainClassLoader
import com.IceCreamQAQ.Yu.loader.transformer.ClassTransformer

object BasicApplicationLauncher {

    @JvmStatic
    fun launch(args: Array<String>) {
        args.iterator().apply {
            while (hasNext())
                when (next()) {
                    "-runMode" -> if (hasNext()) System.setProperty("yu.runMode", next())
                }
        }

        launch()
    }

    @JvmStatic
    fun launch() {
        Application().start()
    }
}
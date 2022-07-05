package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.IceCreamQAQ.Yu.loader.Loader

@LoadBy(AsLoader::class)
interface ApplicationService {

    fun priority() = 10
    fun init()
    fun start()
    fun stop()

}
package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(AsLoader::class)
interface ApplicationService {

    fun init()
    fun start()
    fun stop()

}
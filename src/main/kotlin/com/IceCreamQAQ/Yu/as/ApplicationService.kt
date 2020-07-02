package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(AsLoader::class)
interface ApplicationService {

    fun width() = 10
    fun init()
    fun start()
    fun stop()

}
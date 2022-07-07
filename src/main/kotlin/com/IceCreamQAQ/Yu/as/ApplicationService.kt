package com.IceCreamQAQ.Yu.`as`

import com.IceCreamQAQ.Yu.annotation.LoadBy

@LoadBy(AsLoader::class)
interface ApplicationService {

    fun priority() = 10

    @Deprecated("过时方法", ReplaceWith("priority"))
    fun width() = priority()
    fun init()
    fun start()
    fun stop()

}
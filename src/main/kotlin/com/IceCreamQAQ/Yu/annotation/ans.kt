package com.IceCreamQAQ.Yu.annotation

import com.IceCreamQAQ.Yu.job.JobLoader
import com.IceCreamQAQ.Yu.loader.Loader
import kotlin.reflect.KClass

annotation class MultiInstance


annotation class With(val value: Array<KClass<*>>)
annotation class LoadBy(val value: KClass<out Loader>)

annotation class Synonym(val value: Array<String>)
annotation class Path(val value: String)

@LoadBy(JobLoader::class)
annotation class JobCenter
annotation class Cron(val value: String, val time: Long = 0, val async: Boolean = false)

annotation class NotSearch
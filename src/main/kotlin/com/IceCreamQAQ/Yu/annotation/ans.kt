package com.IceCreamQAQ.Yu.annotation

import com.IceCreamQAQ.Yu.controller.ActionManager
import com.IceCreamQAQ.Yu.controller.PathManager
import com.IceCreamQAQ.Yu.controller.SynonymManager
import com.IceCreamQAQ.Yu.job.JobManagerImpl
import com.IceCreamQAQ.Yu.loader.Loader
import kotlin.reflect.KClass

annotation class MultiInstance
annotation class CreateByPrimaryConstructor


annotation class With(val value: Array<KClass<*>>)
annotation class LoadBy(val value: KClass<out Loader>, val mastBean: Boolean = true)

annotation class Synonym(val value: Array<String>)
annotation class Path(val value: String)
annotation class RouterType(val value: String)

annotation class PathBy(val value: KClass<out PathManager>)
annotation class ActionBy(val value: KClass<out ActionManager>)
annotation class SynonymBy(val value: KClass<out SynonymManager>)

@LoadBy(JobManagerImpl::class)
annotation class JobCenter
annotation class Cron(
        val value: String,
        val time: Long = 0,
        val async: Boolean = false,
        val runWithStart: Boolean = false
)

annotation class NotSearch

annotation class Configuration
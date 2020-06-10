package com.IceCreamQAQ.Yu.annotation

import com.IceCreamQAQ.Yu.job.JobLoader

annotation class MultiInstance

annotation class Synonym(val value: Array<String>)
annotation class Path_(val value: String)

@LoadBy(JobLoader::class)
annotation class JobCenter
annotation class Cron(val value: String, val time: Long = 0, val async: Boolean = false)

annotation class NotSearch
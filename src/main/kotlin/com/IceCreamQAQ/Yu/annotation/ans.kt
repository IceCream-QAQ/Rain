package com.IceCreamQAQ.Yu.annotation

import com.IceCreamQAQ.Yu.job.JobLoader

annotation class MultiInstance
@Repeatable
annotation class Synonym(val name:String)
annotation class Path_(val value:String)
@LoadBy_(JobLoader::class)
annotation class JobCenter
annotation class Cron(val value:String,val time:Long = 0,val async:Boolean = true)

annotation class NotSearch
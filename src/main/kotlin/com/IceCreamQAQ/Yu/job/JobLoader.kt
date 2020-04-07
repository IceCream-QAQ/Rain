package com.IceCreamQAQ.Yu.job

import com.IceCreamQAQ.Yu.annotation.Cron
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem_
import com.IceCreamQAQ.Yu.loader.Loader_
import javax.inject.Inject

class JobLoader : Loader_ {

    @Inject
    private lateinit var context: YuContext

    @Inject
    private lateinit var jobManager: JobManager_

    override fun load(items: Map<String, LoadItem_>) {
        val jobs = ArrayList<Job>()
        for (item in items.values) {
            val instance = context[item.type] ?: continue
            val methods = item.type.methods
            for (method in methods) {
                val cron = method.getAnnotation(Cron::class.java) ?: continue
                val timeStr = cron.value
                var time = 0L
                var cTime = ""
                for (c in timeStr) {
                    if (Character.isDigit(c)) cTime += c
                    else {
                        val cc = cTime.toLong()
                        time += when {
                            c == 'd' -> cc * 1000 * 60 * 60 * 24
                            c == 'h' -> cc * 1000 * 60 * 60
                            c == 'm' -> cc * 1000 * 60
                            c == 's' -> cc * 1000
                            c == 'S' -> cc
                            else -> 0L
                        }
                        cTime = ""
                    }
                }


                val job = Job(time, cron.async, ReflectCronInvoker(instance, method))
                jobs.add(job)
            }
        }
        jobManager.jobs = jobs
    }

}
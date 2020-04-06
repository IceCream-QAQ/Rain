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
    private lateinit var jobManager: JobManager

    override fun load(items: Map<String, LoadItem_>) {
        val jobs = ArrayList<Job>()
        for (item in items.values) {
            val instance = context[item.type] ?: continue
            val methods = item.type.methods
            for (method in methods) {
                val cron = method.getAnnotation(Cron::class.java) ?: continue
                val job = Job(cron.time, cron.async, ReflectCronInvoker(instance, method))
                jobs.add(job)
            }
        }
        jobManager.jobs = jobs
    }

}
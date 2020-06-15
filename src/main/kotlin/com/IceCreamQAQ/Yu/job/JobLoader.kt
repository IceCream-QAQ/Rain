package com.IceCreamQAQ.Yu.job

import com.IceCreamQAQ.Yu.annotation.Cron
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import org.slf4j.LoggerFactory
import javax.inject.Inject

class JobLoader : Loader {
    private val log = LoggerFactory.getLogger(JobLoader::class.java)

    @Inject
    private lateinit var context: YuContext

    @Inject
    private lateinit var jobManager: JobManager

    override fun load(items: Map<String, LoadItem>) {
        val jobs = ArrayList<Job>()
        for (item in items.values) {
            log.info("Register JobCenter: ${item.type.name}.")
            val instance = context[item.type] ?: continue
            val methods = item.type.methods
            for (method in methods) {
                val cron = method.getAnnotation(Cron::class.java) ?: continue
                log.debug("Register Job: ${method.name}.")
                val timeStr = cron.value
                val job = if (timeStr.startsWith("At::")) {
                    val tt = timeStr.split("::")

                    val time = if (tt[1] == "h") 60 * 60 * 1000
                    else 24 * 60 * 60 * 1000

                    Job(time.toLong(), cron.async, ReflectCronInvoker(instance, method),tt[2])
                } else {
                    var time = 0L
                    var cTime = ""
                    for (c in timeStr) {
                        if (Character.isDigit(c)) cTime += c
                        else {
                            val cc = cTime.toLong()
                            time += when (c) {
                                'd' -> cc * 1000 * 60 * 60 * 24
                                'h' -> cc * 1000 * 60 * 60
                                'm' -> cc * 1000 * 60
                                's' -> cc * 1000
                                'S' -> cc
                                else -> 0L
                            }
                            cTime = ""
                        }
                    }
                    Job(time, cron.async, ReflectCronInvoker(instance, method))
                }
                jobs.add(job)
                log.debug("Register Job: ${method.name} Success!")
            }
            log.info("Register JobCenter: ${item.type.name} Success!")
        }
        jobManager.jobs = jobs
    }

}
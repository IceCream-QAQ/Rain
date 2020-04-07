package com.IceCreamQAQ.Yu.job

import java.util.*
import kotlin.collections.ArrayList

class JobManager_ {

    lateinit var jobs:ArrayList<Job>

    private lateinit var asyncTimer: Timer
    private lateinit var syncTimers: ArrayList<Timer>

    fun start(){
        asyncTimer = Timer()
        syncTimers = ArrayList()

        for (job in jobs) {
            if(job.async)asyncTimer.schedule(job,job.time,job.time)
            else {
                val syncTimer = Timer()
                syncTimers.add(syncTimer)
                syncTimer.schedule(job,job.time,job.time)
            }
        }
    }

    fun stop(){

    }

}
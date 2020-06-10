package com.IceCreamQAQ.Yu.job

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import java.util.*
import kotlin.collections.ArrayList

class JobManager : ApplicationService{

    var jobs:ArrayList<Job> = ArrayList()

    private lateinit var asyncTimer: Timer
    private lateinit var syncTimers: ArrayList<Timer>
    override fun init() {

    }

    override fun start(){
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

    override fun stop(){

    }

}
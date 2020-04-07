package com.IceCreamQAQ.Yu.job

import java.util.*
import kotlin.collections.ArrayList

class JobManager_ {

    lateinit var jobs:ArrayList<Job>

    private lateinit var timer: Timer

    fun start(){
        timer = Timer()

        for (job in jobs) {
            timer.schedule(job,job.time,job.time)
        }
    }

    fun stop(){

    }

}
package com.IceCreamQAQ.Yu.job

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.util.DateUtil
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class JobManager : ApplicationService {

    var jobs: ArrayList<Job> = ArrayList()

    private lateinit var asyncTimer: Timer
    private lateinit var syncTimers: ArrayList<Timer>

    @Inject
    private lateinit var dateUtil: DateUtil

    override fun init() {

    }

    override fun start() {
        asyncTimer = Timer()
        syncTimers = ArrayList()

        for (job in jobs) {
            val ft =
                    if (job.at == "") job.time
                    else {
                        val t = job.at
                        val d = t.contains(":")
                        val date = Date()

                        if (d) {
                            val ds = dateUtil.formatDate(date)
                            val dd = dateUtil.parseDateTime("$ds $t:00")
                            var tt = dd.time - date.time
                            if (tt<0) tt += 24 * 60 * 60 * 1000
                            tt
                        } else {
                            val ds = dateUtil.formatDateTime(date)
                            val dd = dateUtil.parseDateTime("${ds.subSequence(0, 14)}$t:00")
                            var tt = dd.time - date.time
                            if (tt<0) tt += 60 * 60 * 1000
                            tt
                        }
                    }

            if (ft < 0) {
                println("d")
            }

            if (job.async) asyncTimer.schedule(job, ft, job.time)
            else {
                val syncTimer = Timer()
                syncTimers.add(syncTimer)
                syncTimer.schedule(job, ft, job.time)
            }
        }
    }

    override fun stop() {
        asyncTimer.cancel()
        for (syncTimer in syncTimers) {
            syncTimer.cancel()
        }
    }

}
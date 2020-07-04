package com.IceCreamQAQ.Yu.job

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.util.DateUtil
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class JobManager : ApplicationService {

    var jobs: ArrayList<Job> = ArrayList()

    private var asyncTimer: Timer? = null
    private lateinit var syncTimers: ArrayList<Timer>
    private val registerTimer = HashMap<String, Timer>()

    @Inject
    private lateinit var dateUtil: DateUtil

    override fun init() {

    }

    @JvmOverloads
    fun registerTimer(runnable: Runnable, firstTime: Long, nextTime: Long? = null): String {
        val id = UUID.randomUUID().toString()
        val syncTimer = Timer()
        registerTimer[id] = (syncTimer)
        if (nextTime == null) syncTimer.schedule(RegisterTask(id, runnable, this, false), firstTime)
        else syncTimer.schedule(RegisterTask(id, runnable, this, true), firstTime, nextTime)
        return id
    }

    fun registerTimer(firstTime: Long, nextTime: Long? = null, function: () -> Unit): String {
        val runnable = Runnable { function() }
        return registerTimer(runnable, firstTime, nextTime)
    }

    @JvmOverloads
    fun registerTimer(runnable: Runnable, atTime: String, always: Boolean = true): String {
        val id = UUID.randomUUID().toString()
        val syncTimer = Timer()
        registerTimer[id] = (syncTimer)
        val t = getTime(atTime)
        if (always) {
            val nt = (if (atTime.contains(":")) 24 * 60 * 60 * 1000 else 60 * 60 * 1000).toLong()
            syncTimer.schedule(RegisterTask(id, runnable, this, true), t, nt)
        } else syncTimer.schedule(RegisterTask(id, runnable, this, false), t)
        return id
    }

    fun registerTimer(atTime: String, always: Boolean = true, function: () -> Unit): String {
        val runnable = Runnable { function() }
        return registerTimer(runnable, atTime, always)
    }

    fun deleteTimer(id: String) {
        registerTimer[id]?.cancel()
        registerTimer.remove(id)
    }

    class RegisterTask(private val id: String, private val runnable: Runnable, private val manager: JobManager, private val next: Boolean) : TimerTask() {
        override fun run() {
            runnable.run()
            if (!next) {
                manager.registerTimer.remove(id)
                this.cancel()
            }
        }
    }

    override fun start() {
        asyncTimer = Timer()
        syncTimers = ArrayList()

        for (job in jobs) {
            val ft =
                    if (job.at == "") job.time
                    else getTime(job.at)

            if (job.async) asyncTimer?.schedule(job, ft, job.time)
            else {
                val syncTimer = Timer()
                syncTimers.add(syncTimer)
                syncTimer.schedule(job, ft, job.time)
            }
        }
    }

    fun getTime(time: String): Long {
        val d = time.contains(":")
        val date = Date()

        return if (d) {
            val ds = dateUtil.formatDate(date)
            val dd = dateUtil.parseDateTime("$ds $time:00")
            var tt = dd.time - date.time
            if (tt < 0) tt += 24 * 60 * 60 * 1000
            tt
        } else {
            val ds = dateUtil.formatDateTime(date)
            val dd = dateUtil.parseDateTime("${ds.subSequence(0, 14)}$time:00")
            var tt = dd.time - date.time
            if (tt < 0) tt += 60 * 60 * 1000
            tt
        }
    }

    override fun stop() {
        asyncTimer?.cancel()
        for (syncTimer in syncTimers) {
            syncTimer.cancel()
        }
        for (timer in registerTimer.values) {
            timer.cancel()
        }
    }

}
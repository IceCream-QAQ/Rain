package com.IceCreamQAQ.Yu.job

import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.*

class Job(
        val time: Long,
        val async: Boolean,
        private val invoker: CronInvoker,
        val runWithStart: Boolean,
        val at: String = ""
) : TimerTask() {

    companion object {
        private val log = LoggerFactory.getLogger(Job::class.java)
    }

    private lateinit var runnable: Runnable

    init {
        if (async) {
            runnable = Runnable { this() }
        }
    }

    operator fun invoke() {
        try {
            invoker()
        } catch (e: Exception) {
            log.error("Job ${invoker.name} Run Exception!", e)
        }
    }

    override fun run() {
        if (async) Thread(runnable).start()
        else this()
    }
}
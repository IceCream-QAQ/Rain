package com.IceCreamQAQ.Yu.job

import java.lang.Exception
import java.util.*

class Job(
        val time: Long,
        val async: Boolean,
        private val invoker: CronInvoker,
        val at: String = ""
) : TimerTask() {

    private lateinit var runnable: Runnable

    init {
        if (async) {
            runnable = Runnable { invoker.invoker() }
        }
    }

    override fun run() {
        try {
            if (async) Thread(runnable).start()
            else invoker.invoker()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
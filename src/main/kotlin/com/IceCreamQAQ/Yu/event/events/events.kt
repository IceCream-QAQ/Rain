package com.IceCreamQAQ.Yu.event.events

import com.IceCreamQAQ.Yu.event.EventListenerInfo
import com.IceCreamQAQ.Yu.job.JobInfo

class EventListenerRunExceptionEvent(
    val listenerInfo: EventListenerInfo,
    val throwable: Throwable
) : Event()

class JobRunExceptionEvent(
    val jobInfo: JobInfo,
    val throwable: Throwable
) : Event()
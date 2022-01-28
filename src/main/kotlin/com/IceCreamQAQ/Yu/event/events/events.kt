package com.IceCreamQAQ.Yu.event.events

import com.IceCreamQAQ.Yu.event.EventListenerInfo

class EventListenerRunExceptionEvent(
    val listenerInfo: EventListenerInfo,
    val throwable: Throwable
) : Event()
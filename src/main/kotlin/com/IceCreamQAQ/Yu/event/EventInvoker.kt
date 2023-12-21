package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.event.events.Event

interface EventInvoker {
    fun invoke(event: Event)
}

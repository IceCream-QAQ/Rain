package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.event.events.Event
import com.IceCreamQAQ.Yu.annotation.Event.Weight
import com.IceCreamQAQ.Yu.event.events.EventListenerRunExceptionEvent
import com.IceCreamQAQ.Yu.fullName
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

class EventBus {

    companion object {
        private val log = LoggerFactory.getLogger(EventBus::class.java)
    }

    @Inject
    private val creator: EventInvokerCreator? = null
    private val eventInvokersLists: Map<Weight, MutableList<EventListenerInfo>> =
        hashMapOf(
            Weight.record to arrayListOf(),
            Weight.highest to arrayListOf(),
            Weight.high to arrayListOf(),
            Weight.normal to arrayListOf(),
            Weight.low to arrayListOf(),
            Weight.lowest to arrayListOf(),
        )

    val ss = arrayOf(Weight.highest, Weight.high, Weight.normal, Weight.low, Weight.lowest)

    fun post(event: Event): Boolean {
        operator fun EventListenerInfo.invoke(): Boolean {
            try {
                invoker.invoke(event)
            } catch (throwable: Throwable) {
                log.error("EventListenerError! At: $methodFullName.", throwable)
                if (event !is EventListenerRunExceptionEvent)
                    post(EventListenerRunExceptionEvent(this, throwable))
            }
            return event.cancelAble() && event.cancel
        }
        eventInvokersLists[Weight.record]!!.forEach { it() }
        event.cancel = false
        for (i in 0..4) {
            val width = ss[i]
            for (eventInvoker in eventInvokersLists[width]!!) {
                if (eventInvoker()) return true
            }
        }
        return false
    }

    fun register(instance: Any) {
        val eventInvokersLists = creator!!.register(instance)
        this.eventInvokersLists[Weight.record]!!.addAll(eventInvokersLists[0])
        for (i in 0..4) {
            val width = ss[i]
            this.eventInvokersLists[width]!!.addAll(eventInvokersLists[i + 1])
        }
    }
}

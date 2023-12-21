package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.annotation.AutoBind
import com.IceCreamQAQ.Yu.event.events.Event
import com.IceCreamQAQ.Yu.annotation.Event.Weight
import com.IceCreamQAQ.Yu.event.events.CancelAbleEvent
import com.IceCreamQAQ.Yu.event.events.EventListenerRunExceptionEvent
import org.slf4j.LoggerFactory
import java.util.*

@AutoBind
interface EventBus {
    fun post(event: Event): Boolean
    fun register(info: EventListenerInfo)
    fun unregister(info: EventListenerInfo)
}

class EventBusImpl : EventBus {

    companion object {
        private val log = LoggerFactory.getLogger(EventBus::class.java)
    }

    private val eis: Map<Weight, MutableList<EventListenerInfo>> =
        hashMapOf(
            Weight.record to arrayListOf(),
            Weight.highest to arrayListOf(),
            Weight.high to arrayListOf(),
            Weight.normal to arrayListOf(),
            Weight.low to arrayListOf(),
            Weight.lowest to arrayListOf(),
        )

    val ss = arrayOf(Weight.highest, Weight.high, Weight.normal, Weight.low, Weight.lowest)

    override fun post(event: Event): Boolean {
        val cancelable = event is CancelAbleEvent
        operator fun EventListenerInfo.invoke(): Boolean {
            try {
                invoker.invoke(event)
            } catch (throwable: Throwable) {
                log.error("EventListenerError! At: $methodFullName.", throwable)
                if (event !is EventListenerRunExceptionEvent)
                    post(EventListenerRunExceptionEvent(this, throwable))
            }
            return cancelable && (event as CancelAbleEvent).isCanceled
        }
        eis[Weight.record]!!.forEach { it() }
        if (cancelable)(event as CancelAbleEvent).isCanceled = false
        for (i in 0..4) {
            val width = ss[i]
            for (eventInvoker in eis[width]!!) {
                if (eventInvoker()) return true
            }
        }
        return false
    }

    override fun register(info: EventListenerInfo) {
        eis[info.weight]!!.add(info)
    }

    override fun unregister(info: EventListenerInfo) {
        eis[info.weight]!!.remove(info)
    }

}

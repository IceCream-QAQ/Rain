package rain.event

import rain.event.events.EventListenerRunExceptionEvent
import org.slf4j.LoggerFactory
import rain.api.event.EventBus
import rain.event.annotation.SubscribeEvent.Weight
import rain.event.events.CancelAbleEvent
import rain.api.event.Event
import rain.function.annotation

class EventBusImpl(
    val creator: EventInvokerCreator
) : EventBus {

    companion object {
        private val log = LoggerFactory.getLogger(EventBus::class.java)
    }

    private val eis: Map<Weight, MutableList<EventListenerInfo>> =
        hashMapOf(
            Weight.RECORD to arrayListOf(),
            Weight.HIGHEST to arrayListOf(),
            Weight.HIGH to arrayListOf(),
            Weight.NORMAL to arrayListOf(),
            Weight.LOW to arrayListOf(),
            Weight.LOWEST to arrayListOf(),
        )

    val ss = arrayOf(Weight.HIGHEST, Weight.HIGH, Weight.NORMAL, Weight.LOW, Weight.LOWEST)

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
        eis[Weight.RECORD]!!.forEach { it() }
        if (cancelable) (event as CancelAbleEvent).isCanceled = false
        for (i in 0..4) {
            val width = ss[i]
            for (eventInvoker in eis[width]!!) {
                if (eventInvoker()) return true
            }
        }
        return false
    }

    fun register(info: EventListenerInfo) {
        eis[info.weight]!!.add(info)
    }

    fun unregister(info: EventListenerInfo) {
        eis[info.weight]!!.remove(info)
    }

    fun <T> register(clazz: Class<T>, instance: T) {
        clazz.methods.forEach {
            it.annotation<rain.event.annotation.SubscribeEvent> {
                register(
                    EventListenerInfo(
                        clazz = clazz,
                        method = it,
                        weight = weight,
                        instance = instance,
                        invoker = creator
                            .createEventHandlerInvokerClass(it)
                            .run { getConstructor(Any::class.java).newInstance(instance) }
                                as EventInvoker
                    )
                )
            }
        }
    }

}
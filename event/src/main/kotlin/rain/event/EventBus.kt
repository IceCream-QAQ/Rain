package rain.event

import rain.event.events.Event

interface EventBus {
    fun post(event: Event): Boolean
    fun register(info: EventListenerInfo)
    fun unregister(info: EventListenerInfo)
    fun <T> register(clazz: Class<T>, instance: T)
}
package rain.event.events

import rain.event.EventListenerInfo

class EventListenerRunExceptionEvent(
    val listenerInfo: EventListenerInfo,
    val throwable: Throwable
) : Event
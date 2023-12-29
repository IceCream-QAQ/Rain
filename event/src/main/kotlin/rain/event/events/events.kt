package rain.event.events

import rain.api.event.Event
import rain.event.EventListenerInfo

class EventListenerRunExceptionEvent(
    val listenerInfo: EventListenerInfo,
    val throwable: Throwable
) : Event
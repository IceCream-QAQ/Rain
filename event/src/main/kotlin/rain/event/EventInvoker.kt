package rain.event

import rain.event.events.Event

interface EventInvoker {
    fun invoke(event: Event)
}

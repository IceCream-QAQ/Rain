package rain.event

import rain.api.event.Event

interface EventInvoker {
    fun invoke(event: Event)
}

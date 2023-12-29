package rain.event.events

import rain.api.event.Event

interface CancelAbleEvent : Event {
    var isCanceled: Boolean
}
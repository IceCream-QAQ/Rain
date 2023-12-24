package rain.event.events

interface CancelAbleEvent : Event {
    var isCanceled: Boolean
}
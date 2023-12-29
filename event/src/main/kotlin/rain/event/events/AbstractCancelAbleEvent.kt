package rain.event.events

open class AbstractCancelAbleEvent : AbstractEvent(), CancelAbleEvent {
    override var isCanceled: Boolean = false
}
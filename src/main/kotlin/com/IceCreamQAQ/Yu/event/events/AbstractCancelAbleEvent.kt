package com.IceCreamQAQ.Yu.event.events

open class AbstractCancelAbleEvent: AbstractEvent(), CancelAbleEvent {
    override var isCanceled: Boolean = false
}
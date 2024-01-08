package rain.api.event

interface EventBus {
    fun post(event: Event): Boolean
}
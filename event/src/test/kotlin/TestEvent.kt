import rain.classloader.SpawnClassLoader
import rain.event.EventBusImpl
import rain.event.EventInvokerCreator
import rain.event.annotation.Event
import rain.event.events.AbstractCancelAbleEvent

class TestEventListener {

    @Event
    fun onTestEvent(event: TestEvent) {
        println("Test event received")
    }

}

class TestEvent : AbstractCancelAbleEvent()

fun main() {
    val classloader = SpawnClassLoader(Thread.currentThread().contextClassLoader)
    val eventBus = EventBusImpl(EventInvokerCreator(classloader))
    eventBus.register(TestEventListener::class.java, TestEventListener())
    eventBus.post(TestEvent())
}
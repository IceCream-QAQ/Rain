package lib

import org.junit.jupiter.api.Test
import rain.classloader.SpawnClassLoader
import rain.event.EventBusImpl
import rain.event.EventInvokerCreator
import rain.event.annotation.SubscribeEvent
import rain.event.events.AbstractCancelAbleEvent


class EventLibTest {
    class TestEventListener {

        @SubscribeEvent
        fun onTestEvent(event: TestEvent) {
            event.cancel()
        }

    }

    class TestEvent : AbstractCancelAbleEvent()

    val eventBus = EventBusImpl(EventInvokerCreator(SpawnClassLoader(this::class.java.classLoader)))

    @Test
    fun event() {
        eventBus.register(TestEventListener::class.java, TestEventListener())
        eventBus.post(TestEvent())
    }
}
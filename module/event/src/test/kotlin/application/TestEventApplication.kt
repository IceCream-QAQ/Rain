package application

import org.junit.jupiter.api.Test
import rain.api.event.Event
import rain.api.event.EventBus
import rain.event.annotation.EventListener
import rain.event.annotation.SubscribeEvent
import rain.test.RainTest

@RainTest
@EventListener
class TestEventApplication(val eventBus: EventBus) {

    var eventInvoked = false

    class TestEvent : Event

    @SubscribeEvent
    fun TestEvent.testHandler() {
        eventInvoked = true
    }

    @Test
    fun testEvent(){
        eventBus.post(TestEvent())
        if (!eventInvoked) throw AssertionError("Event not invoked")
    }

}
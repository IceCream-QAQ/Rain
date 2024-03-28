package application

import rain.event.annotation.EventListener
import rain.event.annotation.SubscribeEvent
import rain.job.JobRunExceptionEvent

@EventListener
class TestEventHandler {

    @SubscribeEvent
    fun onJobError(event: JobRunExceptionEvent) {
        event.error.printStackTrace()
    }


}
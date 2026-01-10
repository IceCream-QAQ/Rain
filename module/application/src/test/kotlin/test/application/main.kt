package test.application

import rain.application.FullStackApplicationLauncher
import rain.application.events.AppStatusEvent
import rain.event.annotation.EventListener
import rain.event.annotation.SubscribeEvent

fun main(){
    FullStackApplicationLauncher.launch()
}

@EventListener
class TestListener {

    @SubscribeEvent
    fun AppStatusEvent.AppStarted.onAppStarted() {
        println("Application has started!")
    }

}
package test.application

import rain.application.FullStackApplicationLauncher
import rain.application.events.AppStatusEvent
import rain.di.YuContext
import rain.di.kotlin.inject
import rain.event.annotation.EventListener
import rain.event.annotation.SubscribeEvent

fun main(){
    FullStackApplicationLauncher.launch()
}

@EventListener
class TestListener {

    val context by inject<YuContext>()

    @SubscribeEvent
    fun AppStatusEvent.AppStarted.onAppStarted() {
        println("Application has started!")
        println("Context: $context")
    }

}
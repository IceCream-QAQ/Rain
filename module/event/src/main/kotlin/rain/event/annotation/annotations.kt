package rain.event.annotation

import rain.api.annotation.LoadBy
import rain.event.EventListenerLoader

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeEvent(val weight: Weight = Weight.NORMAL) {
    enum class Weight {
        LOWEST, LOW, NORMAL, HIGH, HIGHEST, RECORD
    }
}

@Target(AnnotationTarget.CLASS)
@LoadBy(EventListenerLoader::class)
annotation class EventListener
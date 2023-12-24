package rain.event.events

import rain.api.event.Event

interface AppStatusEvent : Event {
    class AppStarted : AppStatusEvent
    class AppStopping : AppStatusEvent
    class AppReloading : AppStatusEvent
}
package rain.event.events

interface AppStatusEvent: Event {
    class AppStarted: AppStatusEvent
    class AppStopping: AppStatusEvent
    class AppReloading: AppStatusEvent
}
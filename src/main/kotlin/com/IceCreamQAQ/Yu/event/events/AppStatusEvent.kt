package com.IceCreamQAQ.Yu.event.events

interface AppStatusEvent: Event {
    class AppStarted: AppStatusEvent
    class AppStopping: AppStatusEvent
    class AppReloading: AppStatusEvent
}
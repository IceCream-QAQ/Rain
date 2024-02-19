package rain.api.event

import rain.api.annotation.AutoBind

@AutoBind
interface EventBus {
    fun post(event: Event): Boolean
}
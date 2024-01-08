package rain.event

import rain.event.annotation.Event
import rain.function.fullName
import java.lang.reflect.Method

data class EventListenerInfo(
    var clazz: Class<*>?,
    val method: Method,
    val weight: Event.Weight,
    val invoker: EventInvoker,
    val instance: Any? = null,
) {
    val methodFullName = method.fullName
}

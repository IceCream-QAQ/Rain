package com.IceCreamQAQ.Yu.event

import com.IceCreamQAQ.Yu.annotation.Event.Weight
import com.IceCreamQAQ.Yu.fullName
import java.lang.reflect.Method

data class EventListenerInfo(
    var clazz: Class<*>?,
    val method: Method,
    val weight: Weight,
    val invoker: EventInvoker,
    val instance: Any? = null,
) {
    val methodFullName = method.fullName
}

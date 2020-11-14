package com.IceCreamQAQ.Yu.event;

import com.IceCreamQAQ.Yu.annotation.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventListenerInfo {

    private Class<?> clazz;
    private Method method;
    private Event.Weight weight;
    private EventInvoker invoker;
    private Object instance;

}

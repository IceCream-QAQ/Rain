package com.IceCreamQAQ.YuQ.event;

import com.IceCreamQAQ.YuQ.event.events.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    private Map<Object, EventHandlerInvoker>  handlers = new ConcurrentHashMap<>();;

    public boolean post(Event event) {
        for (int i = 0; i < 3; i++) {
            for (EventHandlerInvoker value : handlers.values()) {
                value.invoke(event,i);
                if (event.cancelAble() && event.cancel)return true;
            }
        }
        return false;
    }

    public void register(Object object) {
        EventHandlerInvoker handler = handlers.get(object);
        if (handler == null){
            handler=new EventHandlerInvoker(object);
            handlers.put(object,handler);
        }
    }

}

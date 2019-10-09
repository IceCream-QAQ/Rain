package com.IceCreamQAQ.YuQ.event;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.events.Event;
import com.IceCreamQAQ.YuQ.inject.YuQInject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    @Inject
    private YuQInject inject;

    private Integer num = 0;

    Integer getNum() {
        return num++;
    }

    private Map<Object, EventHandlerInvoker> handlers = new ConcurrentHashMap<>();


    public boolean post(Event event) {
        for (int i = 0; i < 3; i++) {
            for (EventHandlerInvoker value : handlers.values()) {
                value.invoke(event, i);
                if (event.cancelAble() && event.cancel) return true;
            }
        }
        return false;
    }

    public void register(Object object) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        EventHandlerInvoker handler = handlers.get(object);
        if (handler == null) {
            handler = inject.spawnInstance(EventHandlerInvoker.class);
            handler.register(object);
            handlers.put(object, handler);
        }
    }

}

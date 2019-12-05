package com.IceCreamQAQ.YuQ.event;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.events.Event;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class EventBus {

    @Inject
    private YuQInject inject;

    @Inject
    private EventInvokerCreator creator;

    private List<EventInvoker>[] eventInvokersLists;
    public EventBus(){
        eventInvokersLists =new List[3];
        for (int i = 0; i < 3; i++) {
            eventInvokersLists[i]=new ArrayList<EventInvoker>();
        }
    }

    public boolean post(Event event) {
        for (val list : eventInvokersLists) {
            for (val eventInvoker : list) {
                eventInvoker.invoke(event);
                if (event.cancelAble() && event.cancel)return true;
            }
        }
        return false;
    }

    public void register(Object object) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
//        EventInvokerCreator handler = handlers.get(object);
//        if (handler == null) {
//            handler = inject.spawnInstance(EventInvokerCreator.class);
//
//            handlers.put(object, handler);
//        }
        val eventInvokersLists= creator.register(object);
        for (int i = 0; i < 3; i++) {
            this.eventInvokersLists[i].addAll(eventInvokersLists[i]);
        }
    }

}

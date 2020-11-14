package com.IceCreamQAQ.Yu.event;

import com.IceCreamQAQ.Yu.event.events.Event;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EventBus {

    @Inject
    private EventInvokerCreator creator;

    private List<EventListenerInfo>[] eventInvokersLists;

    public EventBus() {
        eventInvokersLists = new List[3];
        for (int i = 0; i < 3; i++) {
            eventInvokersLists[i] = new ArrayList<>();
        }
    }

    public boolean post(Event event) {
        for (val list : eventInvokersLists) {
            for (val eventInvoker : list) {
                try {
                    eventInvoker.getInvoker().invoke(event);
                } catch (Throwable throwable) {
                    val errorMessageBuilder = new StringBuilder("EventListenerError! At: ");
                    errorMessageBuilder.append(eventInvoker.getClazz().getName()).append(".").append(eventInvoker.getMethod().getName()).append("(");
                    if (eventInvoker.getMethod().getParameters().length != 0) {
                        val max = eventInvoker.getMethod().getParameters().length - 1;
                        for (int i = 0; i < eventInvoker.getMethod().getParameters().length; i++) {
                            val para = eventInvoker.getMethod().getParameters()[i];
                            errorMessageBuilder.append(para.getType().getSimpleName());
                            if (i != max) errorMessageBuilder.append(", ");
                        }
                    }
                    errorMessageBuilder.append(")");
                    log.error(errorMessageBuilder.toString(), throwable);
                }
                if (event.cancelAble() && event.cancel) return true;
            }
        }
        return false;
    }

    public void register(Object object) {
        val eventInvokersLists = creator.register(object);
        for (int i = 0; i < 3; i++) {
            this.eventInvokersLists[i].addAll(eventInvokersLists[i]);
        }
    }

}

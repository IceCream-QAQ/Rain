package com.IceCreamQAQ.YuQ.loader;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import lombok.val;

import java.util.List;

public class EventHandlerLoader {

    @Inject
    private YuQInject inject;

    @Inject
    private EventBus eventBus;

    public void load(List<Class> classes){
        for (val clazz : classes) {
            eventBus.register(inject.spawnAndPut(clazz,""));
        }
    }

}

package com.IceCreamQAQ.YuQ.loader;

import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import lombok.val;

import java.util.List;

public class EventListenerLoader {

    @Inject
    private YuQInject inject;

    @Inject
    private AppLogger logger;

    @Inject
    private EventBus eventBus;

    public void load(List<Class> classes) {
        for (val clazz : classes) {
            try {
                eventBus.register(inject.spawnAndPut(clazz, ""));
            } catch (Exception e) {
                e.printStackTrace();
                logger.logError("YuQ Loader", "EventHandler " + clazz.getName() + " 注册失败！");
            }
        }
    }

}

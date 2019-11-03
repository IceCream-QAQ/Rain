package com.IceCreamQAQ.YuQ.loader;

import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.EventBus;
import lombok.val;

import java.util.List;

public class EventListenerLoader implements Loader {

    @Inject
    private AppLogger logger;

    @Inject
    private EventBus eventBus;

    @Override
    public void load(List<LoadItem> items) {
        for (val item : items) {
            try {
                eventBus.register(item.getInstance());
            } catch (Exception e) {
                e.printStackTrace();
                logger.logError("YuQ Loader", "EventHandler " + item.getType().getName() + " 注册失败！");
            }
        }
    }
}

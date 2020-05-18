package com.IceCreamQAQ.Yu.event;

import com.IceCreamQAQ.Yu.AppLogger;
import com.IceCreamQAQ.Yu.annotation.Inject;
import com.IceCreamQAQ.Yu.event.EventBus;
import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import lombok.val;

import java.util.List;

@Deprecated
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

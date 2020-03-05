package com.IceCreamQAQ.Yu.event;

import com.IceCreamQAQ.Yu.AppLogger;
import com.IceCreamQAQ.Yu.di.YuContext;
import com.IceCreamQAQ.Yu.loader.LoadItem_;
import com.IceCreamQAQ.Yu.loader.Loader_;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Map;

public class EventListenerLoader_ implements Loader_ {

    @Inject
    private AppLogger logger;

    @Inject
    private EventBus eventBus;

    @Inject
    private YuContext context;

    @Override
    public void load(@NotNull Map<String, LoadItem_> items) {
        for (val item : items.values()) {
            try {
                eventBus.register(context.getBean(item.type, ""));
            } catch (Exception e) {
                e.printStackTrace();
                logger.logError("YuQ Loader", "EventHandler " + item.getType().getName() + " 注册失败！");
            }
        }
    }
}

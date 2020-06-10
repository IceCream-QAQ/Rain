package com.IceCreamQAQ.Yu.event;

import com.IceCreamQAQ.Yu.AppLogger;
import com.IceCreamQAQ.Yu.di.YuContext;
import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Map;

public class EventListenerLoader implements Loader {

    @Inject
    private AppLogger logger;

    @Inject
    private EventBus eventBus;

    @Inject
    private YuContext context;

    @Override
    public void load(@NotNull Map<String, LoadItem> items) {
        for (val item : items.values()) {
            try {
                eventBus.register(context.getBean(item.type, ""));
            } catch (Exception e) {
                e.printStackTrace();
                logger.logError("YuQ Loader", "EventHandler " + item.getType().getName() + " 注册失败！");
            }
        }
    }

    @Override
    public int width() {
        return 10;
    }
}

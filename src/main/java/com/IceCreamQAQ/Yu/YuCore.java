package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.annotation.Inject;
import com.IceCreamQAQ.Yu.event.EventBus;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.event.events.AppStopEvent;
import com.IceCreamQAQ.Yu.inject.YuQInject;
import com.IceCreamQAQ.Yu.loader.ReloadAble;
import com.IceCreamQAQ.Yu.loader.AppLoader;

public class YuCore {

    public YuQInject inject;


    @Inject
    protected EventBus eventBus;

    private AppLoader loader;

    protected boolean enable;

    public YuCore(){}

    public YuCore(AppLogger logger) throws Exception {
        inject = new YuQInject(this.getClass().getClassLoader());

        inject.putInjectObj(YuQInject.class.getName(), "", inject);
        inject.putInjectObj(AppLogger.class.getName(), "", logger);
        inject.putInjectObj(ClassLoader.class.getName(), "appClassLoader", this.getClass().getClassLoader());
    }

    public YuCore(AppLogger logger,ReloadAble reloadAble) throws Exception {
        this(logger);

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);
    }

    public void start() throws Exception {
        loader = inject.spawnInstance(AppLoader.class);
        loader.load_();

        inject.injectObject(this);

        eventBus.post(new AppStartEvent());
    }

    public void stop() {
        eventBus.post(new AppStopEvent());
    }

}

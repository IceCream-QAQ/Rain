package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.event.events.AppDisableEvent;
import com.IceCreamQAQ.YuQ.event.events.AppEnableEvent;
import com.IceCreamQAQ.YuQ.event.events.AppStartEvent;
import com.IceCreamQAQ.YuQ.event.events.AppStopEvent;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import com.IceCreamQAQ.YuQ.loader.AppLoader;

import java.util.List;
import java.util.Map;

public abstract class App {

    public YuQInject inject;


    @Inject
    protected EventBus eventBus;

    private AppLoader loader;

    protected boolean enable;

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger) throws Exception {
        this(reloadAble, yu, logger, App.class.getClassLoader());
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        platformLoad(paras);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, List<Object> list) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        for (Object o : list) {
            inject.putInjectObj(o.getClass().getName(), "", o);
        }

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object[] objects) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        for (Object o : objects) {
            inject.putInjectObj(o.getClass().getName(), "", o);
        }

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object object) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        inject.putInjectObj(object.getClass().getName(), "", object);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, List<Object> list, Map<String, Object> paras) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        for (Object o : list) {
            inject.putInjectObj(o.getClass().getName(), "", o);
        }
        platformLoad(paras);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object[] objects, Map<String, Object> paras) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        for (Object o : objects) {
            inject.putInjectObj(o.getClass().getName(), "", o);
        }
        platformLoad(paras);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Object object, Map<String, Object> paras) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        inject.putInjectObj(object.getClass().getName(), "", object);
        platformLoad(paras);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras, List<Object> list) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        for (Object o : list) {
            inject.putInjectObj(o.getClass().getName(), "", o);
        }
        platformLoad(paras);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras, Object[] objects) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        for (Object o : objects) {
            inject.putInjectObj(o.getClass().getName(), "", o);
        }
        platformLoad(paras);

        start();
    }

    public App(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader, Map<String, Object> paras, Object object) throws Exception {
        init(reloadAble, yu, logger, appClassLoader);

        inject.putInjectObj(object.getClass().getName(), "", object);
        platformLoad(paras);

        start();
    }

    public void init(ReloadAble reloadAble, YuQ yu, AppLogger logger, ClassLoader appClassLoader) throws Exception {
        inject = new YuQInject(appClassLoader);

        inject.putInjectObj(YuQInject.class.getName(), "", inject);
        inject.putInjectObj(YuQ.class.getName(), "", yu);
        inject.putInjectObj(AppLogger.class.getName(), "", logger);
        inject.putInjectObj(ClassLoader.class.getName(), "appClassLoader", appClassLoader);

        inject.injectObject(yu);
        inject.injectObject(logger);

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);
    }

    public void start() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        loader = inject.spawnInstance(AppLoader.class);
        loader.load();

        inject.injectObject(this);

        eventBus.post(new AppStartEvent());
    }

    public void stop() {
        eventBus.post(new AppStopEvent());
    }

    public void enable() {
        eventBus.post(new AppEnableEvent());
        this.enable = true;
    }

    public void disable() {
        eventBus.post(new AppDisableEvent());
        this.enable = false;
    }

    public void platformLoad(Map<String, Object> paras){

    }

}

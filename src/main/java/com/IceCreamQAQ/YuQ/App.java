package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminAddEvent;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminDelEvent;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import com.IceCreamQAQ.YuQ.loader.YuQLoader;
import com.IceCreamQAQ.YuQ.platform.YuQOperater;
import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import lombok.val;
import org.meowy.cqp.jcq.entity.Anonymous;

public abstract class App {

    public YuQInject inject;

    @Inject
    private YuQ yuq;
    @Inject(name = "group")
    private RouteInvoker groupRouter;
    @Inject(name = "priv")
    private RouteInvoker privateRouter;
    @Inject
    private EventBus eventBus;
    @Inject
    private YuQLoader loader;


    public App(ReloadAble reloadAble, YuQOperater operater) throws Exception {
        inject = new YuQInject();
        inject.putInjectObj(inject.getClass().getName(), null, inject);

        inject.putInjectObj(YuQOperater.class.getName(), "", operater);
        inject.spawnAndPut(YuQ.class, "");

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);

        inject.injectObject(this);
    }

    public void start() throws IllegalAccessException, ClassNotFoundException {
        loader.load();
        inject.injectObject(this);
    }




    public int onGroupMessage(int id, long group, long qq, Anonymous noName, String text, int font) throws Exception {
        val texts = text.split(" ");
        val message = Message.buildMessage(id, qq, group, noName, texts,text);

        val context = new ActionContext(message);
        inject.injectObject(context);

        groupRouter.invoke(texts[0], context);

        val reMsg = context.getReMessage();
        if (reMsg != null) yuq.sendMessage(context);

        return context.getIntercept();
    }

    public int onPrivateMessage(int id, long qq, String text, int font) throws Exception {
        val texts = text.split(" ");
        val message = Message.buildMessage(id, qq, null, null, texts,text);

        val context = new ActionContext(message);
        inject.injectObject(context);

        privateRouter.invoke(texts[0], context);

        val reMsg = context.getReMessage();
        if (reMsg != null) yuq.sendMessage(context);

        return context.getIntercept();
    }

    public int groupAdminAdd(int subtype, int time, long group, long qq) {
        val event=new GroupAdminAddEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        eventBus.post(event);

        return 0;
    }

    public int groupAdminDel(int subtype, int time, long group, long qq) {
        val event=new GroupAdminDelEvent();
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        eventBus.post(event);

        return 0;
    }

}

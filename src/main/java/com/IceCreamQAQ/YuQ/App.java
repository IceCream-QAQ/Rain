package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.event.EventBus;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminAddEvent;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminDelEvent;
import com.IceCreamQAQ.YuQ.event.events.GroupAdminEvent;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import com.IceCreamQAQ.YuQ.loader.YuQLoader;
import com.IceCreamQAQ.YuQ.route.Router;
import com.sobte.cqp.jcq.entity.Anonymous;
import com.sobte.cqp.jcq.entity.CoolQ;
import lombok.val;

public class App {

    public YuQInject inject;

    @Inject
    private YuQ yuq;
    @Inject(name = "group")
    private Router groupRouter;
    @Inject(name = "priv")
    private Router privateRouter;
    @Inject
    public EventBus eventBus;


    public App(ReloadAble reloadAble, CoolQ cq) throws Exception {
        inject = new YuQInject();
        inject.putInjectObj(inject.getClass().getName(), null, inject);

        inject.putInjectObj(CoolQ.class.getName(), "", cq);
        inject.spawnAndPut(YuQ.class, "");

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);

        val loader = inject.spawnInstance(YuQLoader.class);
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

    public int groupAdmin(int subtype, int time, long group, long qq) {
        GroupAdminEvent event;
        if (subtype==1){
            event=new GroupAdminDelEvent();
        }else {
            event=new GroupAdminAddEvent();
        }
        event.setGroup(group);
        event.setTime(time);
        event.setQq(qq);

        eventBus.post(event);

        return 0;
    }

}

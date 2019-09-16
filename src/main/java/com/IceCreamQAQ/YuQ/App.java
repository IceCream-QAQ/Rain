package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.controller.ControllerSearcher;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.route.Router;
import com.sobte.cqp.jcq.entity.Anonymous;
import com.sobte.cqp.jcq.entity.CoolQ;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class App {

    public YuQInject inject;

    private YuQ yuq;


    public App(ReloadAble reloadAble, CoolQ cq) throws Exception {
        inject = new YuQInject();
        inject.putInjectObj(inject.getClass().getName(), null, inject);

        inject.putInjectObj(CoolQ.class.getName(), "", cq);
        yuq = inject.spawnAndPut(YuQ.class, "");

        if (reloadAble != null) inject.putInjectObj(ReloadAble.class.getName(), "", reloadAble);

        val controllerSearch = inject.spawnAndPut(ControllerSearcher.class,"");
        val routers = controllerSearch.makeRouter();
        privateRouter = routers[0];
        groupRouter = routers[1];
    }

    private Router groupRouter;
    private Router privateRouter;


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

}

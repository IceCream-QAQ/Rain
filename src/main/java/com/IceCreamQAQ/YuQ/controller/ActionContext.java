package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.inject.ActionContextInject;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import lombok.Getter;
import lombok.Setter;
import lombok.var;

public class ActionContext {

    @Getter
    private Message message;

    @Getter
    @Setter
    private Message reMessage;

    @Getter
    @Setter
    private boolean at = false;

    @Getter
    @Setter
    private boolean re = false;

    @Getter
    @Setter
    private Integer intercept = 0;

    @Inject
    private YuQInject globalInject;

    private ActionContextInject contextInject;

    public ActionContext(Message message) {
        contextInject = new ActionContextInject();

        contextInject.putInjectObj(message.getClass().toString(), "", message);

        contextInject.putInjectObj("java.lang.Long", "qq", message.getQq());
        contextInject.putInjectObj("java.lang.Long", "group", message.getGroup());
        if (message.getNoName() != null)
            contextInject.putInjectObj(message.getNoName().getClass().getName(), "", message.getNoName());
        contextInject.putInjectObj(message.getTexts().getClass().getName(), "", message.getTexts());
        contextInject.putInjectObj(message.getMsg().getClass().getName(), "", message.getMsg());

        this.message = message;
    }

    public void saveObj(Object object) {
        saveObj("", object);
    }

    public void saveObj(String name, Object object) {
        if (name == null) name = "";
        contextInject.putInjectObj(object.getClass().getName(), name, object);
    }

    public Object injectObj(Inject inject, Class<?> clazz) {
        var obj = this.contextInject.getObj(inject, clazz);
        if (obj == null) {
            obj = globalInject.getObj(inject, clazz);
        }
        return obj;
    }

    public <T> T injectObj(Class<T> clazz, String name) {
        var obj = this.contextInject.getObj(clazz.getName(), name);
        if (obj == null) {
            obj = globalInject.getObj(clazz.getName(), name);
        }
        return (T) obj;
    }

}

package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.annotation.PathVar;
import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public abstract class MessageActionContext extends ActionContext {

    @Getter
    private Message message;

    @Getter
    @Setter
    private boolean at = false;

    @Getter
    @Setter
    private boolean re = false;

    @Getter
    @Setter
    private Integer intercept = 0;

    public MessageActionContext(Message message) {
        super(message.getTexts());

        contextInject.putInjectObj(message.getClass().toString(), "", message);
        contextInject.putInjectObj(message.getClass().toString(), "message", message);

        contextInject.putInjectObj(message.getTexts().getClass().getName(), "", message.getTexts());
        contextInject.putInjectObj(message.getMsg().getClass().getName(), "message", message.getMsg());

        this.message = message;
    }

    public Message getReMessage(){
        return (Message) this.getResult();
    }

    @Override
    public <T> T injectPathVar(Class<T> clazz, Integer key, PathVar.Type type) {
        Object para;

        val message = getMessage();
        val texts = message.getTexts();

        switch (type) {
            case string:
                para = texts[key];
                break;
            case qq:
                para = Long.parseLong(texts[key]);
                break;
            case flag:
                val text = texts[key];
                para = text.contains("开") || text.contains("启");
                break;
            case group:
                para = Long.parseLong(texts[key]);
                break;
            case number:
                para = Integer.parseInt(texts[key]);
                break;
            default:
                para = null;
        }

        return (T) para;
    }

}

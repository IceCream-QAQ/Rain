package com.IceCreamQAQ.YuQ.platform.software.QQ;

import com.IceCreamQAQ.YuQ.controller.MessageActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.Getter;
import lombok.Setter;

public class QQMessageActionContext extends MessageActionContext {



    public QQMessageActionContext(QQMessage message) {
        super(message);

        contextInject.putInjectObj("java.lang.Long", "qq", message.getQq());
        if (message.getGroup() != null) contextInject.putInjectObj("java.lang.Long", "group", message.getGroup());
        if (message.getNoName() != null)
            contextInject.putInjectObj(message.getNoName().getClass().getName(), "", message.getNoName());

    }

    @Override
    public Message buildMessage(String text) {
        return new QQMessage.Builder(text).setQQ(((QQMessage)this.getMessage()).getQq()).setGroup(((QQMessage)this.getMessage()).getGroup()).build();
    }
}

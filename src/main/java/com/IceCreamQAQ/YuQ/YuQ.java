package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.platform.YuQOperater;
import lombok.val;
import lombok.var;

public class YuQ {

    @Inject
    private YuQOperater operater;

    public void sendMessage(Message message) {
        val group = message.getGroup();
        val qq = message.getQq();

        val msg = message.getMsg();
        if (group == null) {
            operater.sendPrivateMsg(qq, msg);
        } else {
            operater.sendGroupMsg(group, msg);
        }
    }

    void sendMessage(ActionContext context) {
        val message = context.getMessage();
        val reMessage = context.getReMessage();

        var group = reMessage.getGroup();
        if (group == null) group = message.getGroup();
        var qq = reMessage.getQq();
        if (qq == null) qq = message.getQq();

        val msg = reMessage.getMsg();
        if (group == null) {
            operater.sendPrivateMsg(qq, msg);
        } else {
            operater.sendGroupMsg(group, msg);
        }
    }
}

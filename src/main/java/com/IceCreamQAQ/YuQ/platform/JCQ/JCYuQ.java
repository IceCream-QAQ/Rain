package com.IceCreamQAQ.YuQ.platform.JCQ;

import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.YuQ;
import lombok.val;
import lombok.var;
import org.meowy.cqp.jcq.entity.CoolQ;

public class JCYuQ implements YuQ {


    private CoolQ cq;
    public JCYuQ(CoolQ cq){
        this.cq=cq;
    }

    @Override
    public int sendMessage(Message message) {
        val group = message.getGroup();
        val qq = message.getQq();

        val msg = message.getMsg();
        if (group == null) {
            return cq.sendPrivateMsg(qq, msg);
        } else {
            return cq.sendGroupMsg(group, msg);
        }
    }

    @Override
    public int sendMessage(ActionContext context) {
        val message = context.getMessage();
        val reMessage = context.getReMessage();

        var group = reMessage.getGroup();
        if (group == null) group = message.getGroup();
        var qq = reMessage.getQq();
        if (qq == null) qq = message.getQq();

        val msg = reMessage.getMsg();
        if (group == null) {
            return cq.sendPrivateMsg(qq, msg);
        } else {
            return cq.sendGroupMsg(group, msg);
        }
    }

    @Override
    public int acceptFriendRequest(String requestId,String remarks){
        if (remarks==null)remarks="";
        return cq.setFriendAddRequest(requestId, 1, remarks);
    }

    @Override
    public int refuseFriendRequest(String requestId) {
        return cq.setFriendAddRequest(requestId, 2, null);
    }

    @Override
    public int acceptGroupRequest(String requestId) {
        return cq.setGroupAddRequest(requestId, 2, 1, null);
    }

    @Override
    public int refuseGroupRequest(String requestId) {
        return cq.setGroupAddRequest(requestId, 2, 2, null);
    }

    @Override
    public int acceptJoinGroupRequest(String requestId) {
        return cq.setGroupAddRequest(requestId, 1, 1, null);
    }

    @Override
    public int refuseJoinGroupRequest(String requestId) {
        return cq.setGroupAddRequest(requestId, 1, 2, null);
    }
}

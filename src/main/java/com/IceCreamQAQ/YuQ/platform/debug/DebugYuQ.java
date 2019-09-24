package com.IceCreamQAQ.YuQ.platform.debug;

import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.YuQ;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.val;
import lombok.var;

public class DebugYuQ implements YuQ {

    @Inject
    private AppLogger logger;

    @Override
    public int sendMessage(Message message) {
        val group = message.getGroup();
        val qq = message.getQq();

        val msg = message.getMsg();
        if (group == null) {
            logger.logInfoSend("发送私聊消息", message.toString());
        } else {
            logger.logInfoSend("发送群聊消息", message.toString());
        }

        return 0;
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
            logger.logInfoSend("发送私聊消息", reMessage.toString());
        } else {
            logger.logInfoSend("发送群聊消息", reMessage.toString());
        }

        return 0;
    }

    @Override
    public int acceptFriendRequest(String requestId, String remarks) {
        logger.logInfo("同意好友申请", "RequestId: " + requestId + "，备注: " + (remarks == null ? "无" : remarks));
        return 0;
    }

    @Override
    public int refuseFriendRequest(String requestId) {
        logger.logInfo("拒绝好友申请", "RequestId: " + requestId);
        return 0;
    }

    @Override
    public int acceptGroupRequest(String requestId) {
        logger.logInfo("同意入群申请", "RequestId: " + requestId);
        return 0;
    }

    @Override
    public int refuseGroupRequest(String requestId) {
        logger.logInfo("拒绝入群申请", "RequestId: " + requestId);
        return 0;
    }

    @Override
    public int acceptJoinGroupRequest(String requestId) {
        logger.logInfo("同意群邀请", "RequestId: " + requestId);
        return 0;
    }

    @Override
    public int refuseJoinGroupRequest(String requestId) {
        logger.logInfo("拒绝群邀请", "RequestId: " + requestId);
        return 0;
    }
}

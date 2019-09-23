package com.IceCreamQAQ.YuQ.platform.JCQ;

import com.IceCreamQAQ.YuQ.platform.YuQOperater;
import com.sobte.cqp.jcq.entity.CoolQ;

public class JCQOperater implements YuQOperater {

    private CoolQ cq;

    public JCQOperater(CoolQ cq) {
        this.cq = cq;
    }

    @Override
    public int logDebug(String category, String content) {
        return cq.logDebug(category, content);
    }

    @Override
    public int logInfo(String category, String content) {
        return cq.logInfo(category, content);
    }

    @Override
    public int logInfoRecv(String category, String content) {
        return cq.logInfo(category, content);
    }

    @Override
    public int logInfoSend(String category, String content) {
        return cq.logInfoSend(category, content);
    }

    @Override
    public int logInfoSuccess(String category, String content) {
        return cq.logInfoSuccess(category, content);
    }

    @Override
    public int logWarning(String category, String content) {
        return cq.logWarning(category, content);
    }

    @Override
    public int logError(String category, String content) {
        return cq.logError(category, content);
    }

    @Override
    public int logFatal(String category, String content) {
        return cq.logFatal(category, content);
    }

    @Override
    public int sendPrivateMsg(Long qq, String msg) {
        return cq.sendPrivateMsg(qq, msg);
    }

    @Override
    public int sendGroupMsg(Long group, String msg) {
        return cq.sendGroupMsg(group, msg);
    }

    @Override
    public int setFriendAddRequest(String responseFlag, int backType, String remarks) {
        return cq.setFriendAddRequest(responseFlag, backType, remarks);
    }

    @Override
    public int setGroupAddRequestV2(String responseFlag, int requestType, int backType, String reason) {
        return cq.setGroupAddRequest(responseFlag, requestType, backType, reason);
    }
}

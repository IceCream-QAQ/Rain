package com.IceCreamQAQ.YuQ.platform;

public interface YuQOperater {

    int logDebug(String category, String content);

    int logInfo(String category, String content);

    int logInfoRecv(String category, String content);

    int logInfoSend(String category, String content);

    int logInfoSuccess(String category, String content);

    int logWarning(String category, String content);

    int logError(String category, String content);

    int logFatal(String category, String content);

    int sendPrivateMsg(Long qq, String msg);

    int sendGroupMsg(Long group, String msg);

    int setFriendAddRequest(String responseFlag, int backType, String remarks) ;

    int setGroupAddRequestV2(String responseFlag, int requestType, int backType, String reason);
}

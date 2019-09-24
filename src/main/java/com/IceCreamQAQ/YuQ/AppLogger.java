package com.IceCreamQAQ.YuQ;

public interface AppLogger {
    
    public int logDebug(String category, String content);
    
    public int logInfo(String category, String content);

    public int logInfoRecv(String category, String content);

    public int logInfoSend(String category, String content);

    public int logInfoSuccess(String category, String content);

    public int logWarning(String category, String content);

    public int logError(String category, String content);
    
    public int logFatal(String category, String content);

}

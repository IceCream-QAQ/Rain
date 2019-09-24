package com.IceCreamQAQ.YuQ;

public interface AppLogger {
    
    public int logDebug(String title, String body);
    
    public int logInfo(String title, String body);

    public int logInfoRecv(String title, String body);

    public int logInfoSend(String title, String body);

    public int logInfoSuccess(String title, String body);

    public int logWarning(String title, String body);

    public int logError(String title, String body);
    
    public int logFatal(String title, String body);

}

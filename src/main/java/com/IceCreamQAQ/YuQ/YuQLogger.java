package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.platform.YuQOperater;

public class YuQLogger {

    @Inject
    YuQOperater operater;

    
    public int logDebug(String category, String content) {
        return operater.logDebug(category,content);
    }

    
    public int logInfo(String category, String content) {
        return operater.logInfo(category,content);
    }


    public int logInfoRecv(String category, String content) {
        return operater.logInfoRecv(category,content);
    }


    public int logInfoSend(String category, String content) {
        return operater.logInfoSend(category,content);
    }


    public int logInfoSuccess(String category, String content) {
        return operater.logInfoSuccess(category,content);
    }

    
    public int logWarning(String category, String content) {
        return operater.logWarning(category,content);
    }

    
    public int logError(String category, String content) {
        return operater.logError(category,content);
    }

    
    public int logFatal(String category, String content) {
        return operater.logFatal(category,content);
    }
}

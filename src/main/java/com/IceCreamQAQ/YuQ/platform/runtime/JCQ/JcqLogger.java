package com.IceCreamQAQ.YuQ.platform.runtime.JCQ;

import com.IceCreamQAQ.YuQ.AppLogger;
import org.meowy.cqp.jcq.entity.CoolQ;

public class JcqLogger implements AppLogger {

    private CoolQ cq;
    public JcqLogger(CoolQ cq){
        this.cq=cq;
    }
    
    public int logDebug(String category, String content) {
        return cq.logDebug(category,content);
    }
    
    public int logInfo(String category, String content) {
        return cq.logInfo(category,content);
    }

    public int logInfoRecv(String category, String content) {
        return cq.logInfoRecv(category,content);
    }

    public int logInfoSend(String category, String content) {
        return cq.logInfoSend(category,content);
    }

    public int logInfoSuccess(String category, String content) {
        return cq.logInfoSuccess(category,content);
    }
    
    public int logWarning(String category, String content) {
        return cq.logWarning(category,content);
    }
    
    public int logError(String category, String content) {
        return cq.logError(category,content);
    }
    
    public int logFatal(String category, String content) {
        return cq.logFatal(category,content);
    }
}

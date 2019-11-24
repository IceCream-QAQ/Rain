package com.IceCreamQAQ.YuQ.platform.runtime.debug;

import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.util.DateUtil;

public class DebugLogger implements AppLogger {

    @Inject
    private DateUtil dateUtil;

    @Override
    public int logDebug(String category, String content) {
        outLog(buildLog("Debug",category,content));
        return 0;
    }

    @Override
    public int logInfo(String category, String content) {
        outLog(buildLog("Info",category,content));
        return 0;
    }

    @Override
    public int logInfoRecv(String category, String content) {
        outLog(buildLog("Info Recv",category,content));
        return 0;
    }

    @Override
    public int logInfoSend(String category, String content) {
        outLog(buildLog("Info Send",category,content));
        return 0;
    }

    @Override
    public int logInfoSuccess(String category, String content) {
        outLog(buildLog("Info Success",category,content));
        return 0;
    }

    @Override
    public int logWarning(String category, String content) {
        outLog(buildLog("Warning",category,content));
        return 0;
    }

    @Override
    public int logError(String category, String content) {
        outLog(buildLog("Error",category,content));
        return 0;
    }

    @Override
    public int logFatal(String category, String content) {
        outLog(buildLog("Fatal",category,content));
        return 0;
    }

    String buildLog(String level, String title, String body) {
        return dateUtil.formatDate() + "  --" + level + "--  " + "[" + Thread.currentThread().getName() + "]" + "  " + title + "\t\t\t:" + body;
    }

    void outLog(String log){
        System.out.println(log);
    }
}

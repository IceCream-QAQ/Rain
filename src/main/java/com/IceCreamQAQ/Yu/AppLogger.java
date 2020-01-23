package com.IceCreamQAQ.Yu;

/***
 * 通用Logger
 */
public interface AppLogger {

    /***
     * Debug 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logDebug(String title, String body);

    /***
     * Info 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logInfo(String title, String body);

    /***
     * Info 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logInfoRecv(String title, String body);

    /***
     * Info 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logInfoSend(String title, String body);

    /***
     * Info 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logInfoSuccess(String title, String body);

    /***
     * Warning 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logWarning(String title, String body);

    /***
     * Error 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logError(String title, String body);

    /***
     * Fatal 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    public int logFatal(String title, String body);

}

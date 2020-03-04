package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.annotation.AutoBind;

/***
 * 通用Logger
 */
@AutoBind
public interface AppLogger {

    /***
     * Debug 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    int logDebug(String title, String body);

    /***
     * Info 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    int logInfo(String title, String body);

//    /***
//     * Info 级别的 Log
//     * @param title 标题
//     * @param body 内容
//     * @return 标识
//     */
//    public int logInfoRecv(String title, String body);
//
//    /***
//     * Info 级别的 Log
//     * @param title 标题
//     * @param body 内容
//     * @return 标识
//     */
//    public int logInfoSend(String title, String body);
//
//    /***
//     * Info 级别的 Log
//     * @param title 标题
//     * @param body 内容
//     * @return 标识
//     */
//    public int logInfoSuccess(String title, String body);

    /***
     * Warning 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    int logWarning(String title, String body);

    /***
     * Error 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    int logError(String title, String body);

    /***
     * Fatal 级别的 Log
     * @param title 标题
     * @param body 内容
     * @return 标识
     */
    int logFatal(String title, String body);

}

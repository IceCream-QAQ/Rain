package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.controller.MessageActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;

/***
 * 通用操作类
 */
public interface YuQ {

    /***
     * 发送消息
     * @param message 消息内容
     * @return 消息ID
     */
    int sendMessage(Message message) ;

    int sendMessage(MessageActionContext context) ;
}

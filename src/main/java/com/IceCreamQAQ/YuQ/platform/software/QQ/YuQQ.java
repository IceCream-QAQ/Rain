package com.IceCreamQAQ.YuQ.platform.software.QQ;

import com.IceCreamQAQ.YuQ.YuQ;
import com.IceCreamQAQ.YuQ.controller.MessageActionContext;
import com.IceCreamQAQ.YuQ.entity.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;

/***
 * 通用操作类
 */
public interface YuQQ extends YuQ {

    /***
     * 发送消息
     * @param message 消息内容
     * @return 消息ID
     */
    int sendMessage(Message message) ;

    int sendMessage(MessageActionContext context) ;

    /***
     * 接受好友请求
     * @param requestId 请求ID
     * @param remarks 好友备注（无备注时填null）
     * @return 标识
     */
    int acceptFriendRequest(String requestId, String remarks);

    /***
     *
     * @param requestId 请求ID
     * @return 标识
     */
    int refuseFriendRequest(String requestId);

    /***
     * 接受群邀请
     * @param requestId 请求ID
     * @return 标识
     */
    int acceptGroupRequest(String requestId);

    /***
     * 拒绝群邀请
     * @param requestId 请求ID
     * @return 标识
     */
    int refuseGroupRequest(String requestId);

    /***
     * 接受入群申请
     * @param requestId 请求ID
     * @return 标识
     */
    int acceptJoinGroupRequest(String requestId);

    /***
     * 拒绝入群申请
     * @param requestId 请求ID
     * @return 标识
     */
    int refuseJoinGroupRequest(String requestId);
}

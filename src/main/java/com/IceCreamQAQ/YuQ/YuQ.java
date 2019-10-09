package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.val;
import lombok.var;

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

    int sendMessage(ActionContext context) ;

    /***
     * 接受好友请求
     * @param requestId 请求ID
     * @param remarks 好友备注（无备注时填null）
     * @return 标识
     */
    int acceptFriendRequest(String requestId,String remarks);

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

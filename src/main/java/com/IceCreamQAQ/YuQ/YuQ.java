package com.IceCreamQAQ.YuQ;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.val;
import lombok.var;

public interface YuQ {

    int sendMessage(Message message) ;

    int sendMessage(ActionContext context) ;

    int acceptFriendRequest(String requestId,String remarks);

    int refuseFriendRequest(String requestId);

    int acceptGroupRequest(String requestId);

    int refuseGroupRequest(String requestId);

    int acceptJoinGroupRequest(String requestId);

    int refuseJoinGroupRequest(String requestId);
}

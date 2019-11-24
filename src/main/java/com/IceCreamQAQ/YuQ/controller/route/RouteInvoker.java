package com.IceCreamQAQ.YuQ.controller.route;

import com.IceCreamQAQ.YuQ.controller.MessageActionContext;

public interface RouteInvoker {

    void invoke(String path, MessageActionContext context) throws Exception;

}

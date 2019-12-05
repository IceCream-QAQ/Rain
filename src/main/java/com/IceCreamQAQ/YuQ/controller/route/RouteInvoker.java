package com.IceCreamQAQ.YuQ.controller.route;

import com.IceCreamQAQ.YuQ.controller.ActionContext;

public interface RouteInvoker {

    void invoke(String path, ActionContext context) throws Exception;

}

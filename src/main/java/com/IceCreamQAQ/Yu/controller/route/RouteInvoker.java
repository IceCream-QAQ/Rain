package com.IceCreamQAQ.Yu.controller.route;

import com.IceCreamQAQ.Yu.controller.ActionContextBase;

public interface RouteInvoker {

    void invoke(String path, ActionContextBase context) throws Exception;

}

package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.entity.Result;
import lombok.Data;
import lombok.val;

@Data
public abstract class ActionInvoker implements RouteInvoker {

    protected MethodInvoker[] befores;
    protected MethodInvoker invoker;

    @Override
    public abstract void invoke(String path, ActionContext context) throws Exception;
}

package com.IceCreamQAQ.Yu.controller;

import com.IceCreamQAQ.Yu.controller.route.RouteInvoker;
import lombok.Data;

@Deprecated
@Data
public abstract class ActionInvoker implements RouteInvoker {

    protected MethodInvoker[] befores;
    protected MethodInvoker invoker;

    @Override
    public abstract void invoke(String path, ActionContextBase context) throws Exception;
}

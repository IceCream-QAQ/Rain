package com.IceCreamQAQ.Yu.controller.route;

import com.IceCreamQAQ.Yu.controller.ActionContextBase;
import lombok.Getter;
import lombok.val;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Router implements RouteInvoker {

    private Integer level;
    private Map<String, RouteInvoker> routers;

    public Router(Integer level) {
        routers = new ConcurrentHashMap<>();
        this.level = level;
    }

    @Override
    public void invoke(String path, ActionContextBase context) throws Exception {
        if (context.getPath().length==level)return;
        val nextPath = context.getPath()[level];
        val invoker = routers.get(nextPath);
        if (invoker != null) {
            invoker.invoke(nextPath, context);
        }
    }
}

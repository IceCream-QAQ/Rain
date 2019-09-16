package com.IceCreamQAQ.YuQ.route;

import com.IceCreamQAQ.YuQ.controller.ActionContext;
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
    public void invoke(String path, ActionContext context) throws Exception {
        val message = context.getMessage();
        val nextPath = message.getTexts()[level];
        val invoker = routers.get(nextPath);
        if (invoker != null) {
            invoker.invoke(nextPath, context);
        }
    }
}

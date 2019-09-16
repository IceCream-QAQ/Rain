package com.IceCreamQAQ.YuQ.route;

import com.IceCreamQAQ.YuQ.controller.ActionContext;
import com.IceCreamQAQ.YuQ.entity.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Route {//implements RouteInvoker{

    public Map<String,RouteInvoker> routers;

    public Route(){
        routers=new ConcurrentHashMap<>();
    }

    public ActionContext invoke(Message message){
//        val route = routers.get(path);
        return null;
    }

}

package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.route.RouteInvoker;
import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ControllerInvoker implements RouteInvoker {

    MethodInvoker[] befores;
    Map<String, ActionInvoker> actions;

    @Override
    public void invoke(String path, ActionContext context) throws Exception {

        val actionInvoker = actions.get(path);
        val reMessage = invokeAction(context, actionInvoker);

        context.setReMessage(reMessage);
    }

    public Message invokeAction(ActionContext context, ActionInvoker action) throws Exception {
        try {
            for (MethodInvoker before : befores) {
                val reObj = before.invoker(context);
                if (reObj != null) context.saveObj(reObj);
            }

            return action.invoker(context);
//
//            if (reObj instanceof String) return new Message.Builder((String) reObj).setQQ(context.getMessage().getQq()).setGroup(context.getMessage().getGroup()).build();
//            if (reObj instanceof Message) return (Message) reObj;
//            return new Message.Builder(reObj.toString()).build();
        } catch (InvocationTargetException e) {
            val cause = e.getCause();
            if (cause instanceof Message) return (Message) cause;
            throw (Exception) e.getCause();
        } catch (Exception e) {
            return null;
        }
    }

}

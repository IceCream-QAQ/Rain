package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.entity.DoNone;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.route.RouteInvoker;
import com.sobte.cqp.jcq.entity.CoolQ;
import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ControllerInvoker implements RouteInvoker {

    public MethodInvoker[] befores;
    public Map<String, ActionInvoker> actions;

    @Inject
    private CoolQ cq;

    @Override
    public void invoke(String path, ActionContext context) {

        val actionInvoker = actions.get(path);
        val reMessage = invokeAction(context, actionInvoker);

        context.setReMessage(reMessage);
    }

    public Message invokeAction(ActionContext context, ActionInvoker action) {
        try {
            for (MethodInvoker before : befores) {
                val reObj = before.invoker(context);
                if (reObj != null) context.saveObj(reObj);
            }

            return action.invoker(context);
        } catch (InvocationTargetException e) {
            val cause = (Exception)e.getCause();
            if (cause instanceof Message) return (Message) cause;
            if (cause instanceof DoNone) return null;
            cause.printStackTrace();
            cq.logInfo("aaa","bbb");
            val errorMessage= new StringBuilder("程序运行时时异常！\n异常信息：");
            errorMessage.append(cause.getClass().getName()).append(" : ").append(cause.getMessage()).append("\n异常栈：");
            for (val item : cause.getStackTrace()) {
                errorMessage.append("\n").append(item.toString());
            }
            
            cq.logError("YuQ Runtime",  errorMessage.toString());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

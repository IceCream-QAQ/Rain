package com.IceCreamQAQ.YuQ.controller;


import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.annotation.Inject;
import com.IceCreamQAQ.YuQ.entity.DoNone;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import com.IceCreamQAQ.YuQ.entity.Result;
import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ControllerInvoker implements RouteInvoker {

    public MethodInvoker[] befores;
    public Map<String, ActionInvoker> actions;

    @Inject
    private AppLogger logger;

    @Override
    public void invoke(String path, ActionContext actionContext) {

        val context=actionContext;

        val actionInvoker = actions.get(path);
        val reMessage = invokeAction(context, actionInvoker);

        context.setResult(reMessage);
    }

    public Result invokeAction(ActionContext context, ActionInvoker action) {
        try {
            for (val before : befores) {
                val reObj = before.invoke(context);
                if (reObj != null) context.saveObj(reObj);
            }

//            return action.invoke(context);
            return null;
        } catch (InvocationTargetException e) {
            val cause = (Exception)e.getCause();
            if (cause instanceof Message) return (Message) cause;
            if (cause instanceof DoNone) return null;
            cause.printStackTrace();
            logger.logInfo("aaa","bbb");
            val errorMessage= new StringBuilder("程序运行时时异常！\n异常信息：");
            errorMessage.append(cause.getClass().getName()).append(" : ").append(cause.getMessage()).append("\n异常栈：");
            for (val item : cause.getStackTrace()) {
                errorMessage.append("\n").append(item.toString());
            }
            
            logger.logError("YuQ Runtime",  errorMessage.toString());
            return null;
        }catch (DoNone ignored){
            return null;
        }catch (Message message){
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

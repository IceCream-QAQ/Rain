package com.IceCreamQAQ.Yu.controller;


import com.IceCreamQAQ.Yu.AppLogger;
import com.IceCreamQAQ.Yu.annotation.Inject;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.IceCreamQAQ.Yu.controller.route.RouteInvoker;
import com.IceCreamQAQ.Yu.entity.Result;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ControllerInvoker implements RouteInvoker {

    public MethodInvoker[] befores;
    public Map<String, ActionInvoker> actions;

    @Inject
    private AppLogger logger;

    @Override
    public void invoke(String path, ActionContextBase actionContext) {

        val context=actionContext;

        val actionInvoker = actions.get(path);
        val reMessage = invokeAction(context, actionInvoker);

        context.setResult(reMessage);
    }

    public Result invokeAction(ActionContextBase context, ActionInvoker action) {
        try {
            for (val before : befores) {
                val reObj = before.invoke(context);
                if (reObj != null) context.saveObj(reObj);
            }

//            return action.invoke(context);
            return null;
        } catch (InvocationTargetException e) {
            val cause = (Exception)e.getCause();
            if (cause instanceof DoNone) return null;
            if (cause instanceof Result) return (Result) cause;
            cause.printStackTrace();
            val errorMessage= new StringBuilder("程序运行时时异常！\n异常信息：");
            errorMessage.append(cause.getClass().getName()).append(" : ").append(cause.getMessage()).append("\n异常栈：");

            for (val item : cause.getStackTrace()) {
                errorMessage.append("\n").append(item.toString());
            }

            logger.logError("YuQ Runtime",  errorMessage.toString());
            return null;
        }catch (DoNone ignored){
            return null;
        }catch (Result result){
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

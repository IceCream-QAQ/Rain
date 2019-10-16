package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.Data;
import lombok.val;

@Data
public class ActionInvoker {

    private MethodInvoker invoker;

    private Boolean at;
    private Boolean re;
    private Boolean intercept;


    public Message invoke(ActionContext context) throws Exception {
        try {
            context.setAt(at);
            context.setRe(re);
            if (intercept)context.setIntercept(1);
            else context.setIntercept(0);

            val reObj = invoker.invoke(context);

            if (reObj == null) return null;

            if (reObj instanceof String) return new Message.Builder((String) reObj).setQQ(context.getMessage().getQq()).setGroup(context.getMessage().getGroup()).build();
            if (reObj instanceof Message) return (Message) reObj;
            return new Message.Builder(reObj.toString()).build();

        }  catch (Exception e) {
            throw e;
        }
    }

}

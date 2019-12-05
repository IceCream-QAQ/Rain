package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.entity.DoNone;
import com.IceCreamQAQ.YuQ.entity.Message;
import com.IceCreamQAQ.YuQ.entity.Result;
import lombok.val;

public class DefaultActionInvoker extends ActionInvoker {
    @Override
    public void invoke(String path, ActionContext context) throws Exception {
        try {
            for (MethodInvoker before : befores) {
                before.invoke(context);
            }

            val reObj = invoker.invoke(context);

            context.setSuccess(true);

            if (reObj == null) context.setResult(null);

            if (reObj instanceof String) context.buildResult((String) reObj);
            else if (reObj instanceof Result) context.setResult((Result) reObj);
            else context.buildResult(reObj);

        } catch (DoNone ignored) {
        } catch (Message message) {
            context.setResult(message);
        }
    }
}

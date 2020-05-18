package com.IceCreamQAQ.Yu.controller;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.IceCreamQAQ.Yu.entity.Result;
import lombok.val;

@Deprecated
public class DefaultActionInvoker extends ActionInvoker {
    @Override
    public void invoke(String path, ActionContextBase context) throws Exception {
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
        } catch (Result result) {
            context.setResult(result);
        }
    }
}

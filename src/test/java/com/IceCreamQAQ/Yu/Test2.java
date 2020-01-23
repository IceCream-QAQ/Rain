package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

public class Test2 {

    public String b_IceCreamQAQ_YuHook(String name) {
        System.out.println("fun b run !");
        return "Hello " + name;
    }

    public String b(String name) throws Throwable {
        HookMethod hookMethod = new HookMethod();
        hookMethod.className = "com.IceCreamQAQ.Yu.TestA";
        hookMethod.methodName = "b";

        Object[] paras = new Object[2];
        paras[0] = this;
        paras[1] = name;

        hookMethod.paras=paras;

        HookRunnable r = YuHook.r[0];
        if (r.preRun(hookMethod)) return (String) hookMethod.result;
        try {
            hookMethod.result = b_IceCreamQAQ_YuHook((String) paras[1]);
            r.postRun(hookMethod);
            return (String) hookMethod.result;
        } catch (Throwable throwable) {
            hookMethod.error = throwable;
            if (r.onError(hookMethod)) return (String) hookMethod.result;
            throw hookMethod.error;
        }
    }

}

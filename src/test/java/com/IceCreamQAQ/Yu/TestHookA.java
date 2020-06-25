package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

public class TestHookA {


    public static String c_IceCreamQAQ_YuHook(String name, String nn, String nm) {
        System.out.println("fun c run ! name = " + name);
        return "Hello " + name + "!";
    }

    public static String c(String s, String s2, String s3) throws Throwable {
        HookMethod hookMethod = new HookMethod();
        hookMethod.className = "com.IceCreamQAQ.Yu.TestA";
        hookMethod.methodName = "c";
        Object[] paras = new Object[4];
        paras[0] = null;
        paras[1] = s;
        paras[2] = s2;
        paras[3] = s3;
        hookMethod.paras = paras;
        HookRunnable hookRunnable = YuHook.getInvoker("com.IceCreamQAQ.Yu.TestA", "c");
        if (hookRunnable.preRun(hookMethod)) {
            return (String) hookMethod.result;
        }
        try {
            hookMethod.result = c_IceCreamQAQ_YuHook((String) paras[1], (String) paras[2], (String) paras[3]);
            hookRunnable.postRun(hookMethod);
            return (String) hookMethod.result;
        } catch (Throwable error) {
            hookMethod.error = error;
            if (hookRunnable.onError(hookMethod)) {
                return (String) hookMethod.result;
            }
            throw hookMethod.error;
        }
    }


}

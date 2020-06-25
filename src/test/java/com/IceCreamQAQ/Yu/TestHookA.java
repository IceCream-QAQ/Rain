package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

public class TestHookA {

    public static int c_IceCreamQAQ_YuHook(int[] s2, int s, String[] s3) {
        System.out.println("fun c run ! name = " + s);
        return s;
    }

    public static int c(int[] s2, int s, String[] s3) throws Throwable {
        HookMethod hookMethod = new HookMethod();
        hookMethod.className = "com.IceCreamQAQ.Yu.TestA";
        hookMethod.methodName = "c";
        Object[] paras = new Object[4];
        paras[0] = null;
        paras[1] = s2;
        paras[2] = s;
        paras[3] = s3;
        hookMethod.paras = paras;
        HookRunnable hookRunnable = YuHook.getInvoker("com.IceCreamQAQ.Yu.TestA", "c");
        if (hookRunnable.preRun(hookMethod)) {
            return (int) hookMethod.result;
        }
        try {
            hookMethod.result = c_IceCreamQAQ_YuHook((int[]) paras[1], (int) paras[2], (String[]) paras[3]);
            hookRunnable.postRun(hookMethod);
            return (int) hookMethod.result;
        } catch (Throwable error) {
            hookMethod.error = error;
            if (hookRunnable.onError(hookMethod)) {
                return (int) hookMethod.result;
            }
            throw hookMethod.error;
        }
    }


}

package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

public class Test2 {

    public Object b_IceCreamQAQ_YuHook(String name,int a,boolean b) {
        System.out.println("fun b run !");
        return "Hello " + name;
    }

    public Object b(String name,int a,boolean b) throws Throwable {
        HookMethod hookMethod = new HookMethod();
        hookMethod.className = "com.IceCreamQAQ.Yu.TestA";
        hookMethod.methodName = "b";

        Object[] paras = {this,name,a,b};

        hookMethod.paras=paras;

        HookRunnable r = YuHook.r[0];
        if (r.preRun(hookMethod)) return (String) hookMethod.result;
        try {
            hookMethod.result = b_IceCreamQAQ_YuHook((String) paras[1],(Integer) paras[2],(Boolean) paras[3]);
            r.postRun(hookMethod);
            return (String) hookMethod.result;
        } catch (Throwable throwable) {
            hookMethod.error = throwable;
            if (r.onError(hookMethod)) return (String) hookMethod.result;
            throw hookMethod.error;
        }
    }

}

package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

public class Test {

    public String b_IceCreamQAQ_YuHook(){
        System.out.println("fun b run !");
        return null;
    }

    public String b() throws Throwable {
        HookMethod hookMethod=new HookMethod();

        HookRunnable r = YuHook.r[0];
        if (r.preRun(hookMethod))return (String) hookMethod.result;
        try {
            hookMethod.result = b_IceCreamQAQ_YuHook();
            r.postRun(hookMethod);
            return (String) hookMethod.result;
        }catch (Throwable throwable){
            hookMethod.error=throwable;
            if (r.onError(hookMethod))return (String) hookMethod.result;
            throw hookMethod.error;
        }
    }

}

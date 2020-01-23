package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

public class TestA {

    public void a() {
        TestB.o();
    }

    public String b() {
        System.out.println("fun b run !");
        return null;
    }

//    public Object b() throws Throwable {
//        HookMethod hookMethod=new HookMethod();
//        HookRunnable r = YuHook.r[0];
//        if (r.preRun(hookMethod))return hookMethod.result;
//        try {
//            hookMethod.result = b_IceCreamQAQ_YuHook();
//            if (r.postRun(hookMethod))throw hookMethod.error;
//            return hookMethod.result;
//        }catch (Throwable throwable){
//            hookMethod.error=throwable;
//            if (r.onError(hookMethod))return hookMethod.result;
//            throw hookMethod.error;
//        }
//    }

    public Object c(String name) {
        System.out.println("fun c run !");
        return "Hello " + name + "!";
    }
}

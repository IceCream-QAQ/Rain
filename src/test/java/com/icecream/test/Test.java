package com.icecream.test;

import com.IceCreamQAQ.Yu.hook.HookInvokerRunnable;
import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookInfo;

public class Test {

//    private static HookInfo info;
//
//    static {
//        Test.initFun();
//    }
//
//    private static void initFun() {
//        info = HookInfo.create(Test.class, "getC", new Class[]{String.class, Object.class, int.class, Integer[].class, Long[][].class, short[][][].class});
//    }
//
//    public int getC_YuHook(String msg) {
//        return 2;
//    }
//
//    public final long getC(String msg) throws Throwable {
//        final HookMethod hookMethod = new HookMethod();
//        hookMethod.info = info;
//        hookMethod.paras = new Object[]{this, msg};
//        final HookInvokerRunnable invoker = info.runnable;
//        if (invoker.preRun(hookMethod)) {
//            return (long) hookMethod.result;
//        }
//        try {
//            hookMethod.result = this.getC_YuHook((String) hookMethod.paras[1]);
//            invoker.postRun(hookMethod);
//            return (long) hookMethod.result;
//        } catch (Throwable error) {
//            hookMethod.error = error;
//            if (invoker.onError(hookMethod)) {
//                return (long) hookMethod.result;
//            }
//            throw hookMethod.error;
//        }
//    }


}

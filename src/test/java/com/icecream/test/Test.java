package com.icecream.test;

import com.IceCreamQAQ.Yu.hook.HookInvokerRunnable;
import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookInfo;
import com.icecreamqaq.test.yu.HookTa;

public class Test {

    private static HookInfo getC_HookInfo_Static;
    private HookInfo getC_HookInfo_Instance;

    static {
        Test.initFun();
    }

    private static void initFun() {
        getC_HookInfo_Static = HookInfo.create(Test.class, "getC", "getC_YuHook", new Class[]{String.class, Object.class, int.class, Integer[].class, Long[][].class, short[][][].class});
    }

    public int getC_YuHook(String msg) {
        return 2;
    }

    public void setHookTa(HookTa hookTa){

    }

    public final long getC(String msg) throws Throwable {
        final HookMethod hookMethod = new HookMethod();
        hookMethod.info = getC_HookInfo_Static;
        hookMethod.paras = new Object[]{this, msg};
        final HookInvokerRunnable invoker = getC_HookInfo_Static.runnable;
        if (invoker.preRun(hookMethod)) {
            return (long) hookMethod.result;
        }
        try {
            hookMethod.result = this.getC_YuHook((String) hookMethod.paras[1]);
            invoker.postRun(hookMethod);
            return (long) hookMethod.result;
        } catch (Throwable error) {
            hookMethod.error = error;
            if (invoker.onError(hookMethod)) {
                return (long) hookMethod.result;
            }
            throw hookMethod.error;
        }
    }


}

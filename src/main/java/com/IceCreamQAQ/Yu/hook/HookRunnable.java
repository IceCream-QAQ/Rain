package com.IceCreamQAQ.Yu.hook;

public interface HookRunnable {

    boolean preRun(HookMethod method);
    void postRun(HookMethod method);
    boolean onError(HookMethod method);

}

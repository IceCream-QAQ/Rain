package com.IceCreamQAQ.Yu.hook;

public interface HookRunnable {

    void init(HookInfo info);

    boolean preRun(HookMethod method);
    void postRun(HookMethod method);
    boolean onError(HookMethod method);

}

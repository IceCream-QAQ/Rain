package com.IceCreamQAQ.Yu.hook;

import java.util.ArrayList;
import java.util.List;

public class HookInvokerRunnable {

    private final List<HookRunnable> runnables = new ArrayList<>();

    public void put(HookRunnable runnable){
        this.runnables.add(runnable);
    }

    public boolean preRun(HookMethod method) {
        for (HookRunnable runnable : runnables) {
            if (runnable.preRun(method))return true;
        }
        return false;
    }

    public void postRun(HookMethod method) {
        for (HookRunnable runnable : runnables) {
            runnable.postRun(method);
        }
    }

    public boolean onError(HookMethod method) {
        for (HookRunnable runnable : runnables) {
            if (runnable.onError(method))return true;
        }
        return false;
    }
}

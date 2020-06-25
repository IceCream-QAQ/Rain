package com.IceCreamQAQ.Yu.hook;

import java.util.ArrayList;
import java.util.List;

public class HookInvokerRunnable implements HookRunnable {

    private final List<HookRunnable> runnables = new ArrayList<>();

    public void put(HookRunnable runnable){
        this.runnables.add(runnable);
    }

    @Override
    public boolean preRun(HookMethod method) {
        for (HookRunnable runnable : runnables) {
            if (runnable.preRun(method))return true;
        }
        return false;
    }

    @Override
    public void postRun(HookMethod method) {
        for (HookRunnable runnable : runnables) {
            runnable.postRun(method);
        }
    }

    @Override
    public boolean onError(HookMethod method) {
        for (HookRunnable runnable : runnables) {
            if (runnable.onError(method))return true;
        }
        return false;
    }
}

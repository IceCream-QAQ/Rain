package com.IceCreamQAQ.Yu.hook;

import java.util.ArrayList;
import java.util.List;

public class HookInvokerRunnable {

    private final List<HookRunnable> runnables = new ArrayList<>();
    private final List<HookInfo> infos = new ArrayList<>();

    public void put(HookRunnable runnable) {
        this.runnables.add(runnable);
        for (HookInfo info : infos) {
            runnable.init(info);
        }
    }

    public void init(HookInfo info) {
        infos.add(info);
        for (HookRunnable runnable : runnables) {
            runnable.init(info);
        }
    }

    public boolean preRun(HookContext method) {
        for (HookRunnable runnable : runnables) {
            if (runnable.preRun(method)) return true;
        }
        return false;
    }

    public void postRun(HookContext method) {
        for (HookRunnable runnable : runnables) {
            runnable.postRun(method);
        }
    }

    public boolean onError(HookContext method) {
        for (HookRunnable runnable : runnables) {
            if (runnable.onError(method)) return true;
        }
        return false;
    }
}

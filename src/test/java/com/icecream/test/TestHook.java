package com.icecream.test;

import com.IceCreamQAQ.Yu.hook.HookInfo;

import javax.inject.Inject;

public class TestHook extends TestSuper {

    public TestHook() {
        super("");
    }

    public TestHook(String a) {
        super(a);
    }

    private static HookInfo a2;
    private HookInfo a1;

    private static void initStandardHook() {
//        a2 = HookInfo.create(TestHook.class, "staticHook", "staticHook_YuHookV2_OldMethod_a2", new Class[]{});
    }

    private void initInstanceHook() {
//        a1 = HookInfo.instanceCreate(TestHook.class, "hook", "hook_YuHookV2_OldMethod_1", new Class[]{});
    }

    @Inject
    public void setTestHookRunnable(TestHookRunnable runnable) {
        a1.putRunnable(runnable);
    }

    public String hook() {
        return "";
    }

    public String hook_YuHookV2_OldMethod_1() {
        return "";
    }

    public static int staticHook() {
        return 0;
    }

    public static int staticHook_YuHookV2_OldMethod_a2() {
        return 0;
    }

}

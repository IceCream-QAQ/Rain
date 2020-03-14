package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

import javax.inject.Named;

public class TestA {

    public Object c(@Named("name") String name) {
        System.out.println("fun c run ! name = " + name);
        return "Hello " + name + "!";
    }
}

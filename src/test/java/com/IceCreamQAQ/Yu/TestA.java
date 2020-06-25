package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.NewDefaultController;
import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;

import javax.inject.Named;

//@NewDefaultController
public class TestA {

    @Action("123")
    public static String c(String name, String nn,String nm) {
        System.out.println("fun c run ! name = " + name);
        return "Hello " + name + "!";
    }
}

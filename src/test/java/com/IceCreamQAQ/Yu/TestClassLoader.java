package com.IceCreamQAQ.Yu;

import com.IceCreamQAQ.Yu.hook.HookItem;
import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import com.IceCreamQAQ.Yu.hook.YuHook;
import com.IceCreamQAQ.Yu.loader.AppClassloader;
import lombok.val;
import lombok.var;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestClassLoader {


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        YuHook.put(new HookItem("com.IceCreamQAQ.Yu.TestA", "c", HookA.class.getName()));

        val a = new AppClassloader(TestClassLoader.class.getClassLoader());
        Class t = a.loadClass("com.IceCreamQAQ.Yu.TestA");
        var o = t.newInstance();

        var m = t.getMethod("c", String.class, String.class,String.class);

        val r = m.invoke(o, "123","233","456");

        System.out.println(r);

    }

    public static class HookA implements HookRunnable {

        @Override
        public boolean preRun(HookMethod method) {

            val paras = method.paras;
            System.out.println("================================");
            for (Object para : paras) {
                System.out.println("Para: " + para);
            }
            System.out.println("================================");
            paras[1] = "Yu";
            return false;
        }

        @Override
        public void postRun(HookMethod method) {
        }

        @Override
        public boolean onError(HookMethod method) {
            return true;
        }
    }
}

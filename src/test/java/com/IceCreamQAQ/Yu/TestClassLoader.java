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

//        YuHook.put(new HookItem("com.IceCreamQAQ.Yu.TestA", "c", HookA.class.getName()));
//
//        val a = new AppClassloader(TestClassLoader.class.getClassLoader(),null);
//        Class t = a.loadClass("com.IceCreamQAQ.Yu.TestA");
//        var o = t.newInstance();
//
//        var m = t.getMethod("c",String.class);
//
//        val r = m.invoke(o,"123");
//
//        System.out.println(r);

    }

    public static class HookA implements HookRunnable {

        @Override
        public boolean preRun(HookMethod method) {
            System.out.println("fun c preRun !");
            System.out.println("Class Name: " + method.className);
            System.out.println("Method Name: " + method.methodName);

            val paras= method.paras;
            System.out.println("-----------------");
            for (Object para : paras) {
                System.out.println(para);
            }
            System.out.println("-----------------");

            paras[1]="Yu";
            return false;
        }

        @Override
        public void postRun(HookMethod method) {
            System.out.println("fun c postRun !");
            method.result = "Hello YuHook !";
//            throw new RuntimeException();
//            return false;
        }

        @Override
        public boolean onError(HookMethod method) {
            System.out.println("fun c onError !");
            return true;
        }
    }
}

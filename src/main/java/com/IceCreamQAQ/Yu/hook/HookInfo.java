package com.IceCreamQAQ.Yu.hook;

import lombok.val;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HookInfo {

    public final String className;
    public final String methodName;

    public final Class clazz;
    public final Method method;
    public final Class[] methodParas;

    public final HookInvokerRunnable runnable;

    private Map<String,Object> saves;

    public HookInfo(String className, String methodName, Class clazz, Method method, Class[] methodParas, HookInvokerRunnable runnable) {
        this.className = className;
        this.methodName = methodName;
        this.clazz = clazz;
        this.method = method;
        this.methodParas = methodParas;
        this.runnable = runnable;
    }

    public static HookInfo create(Class clazz, String methodName, Class[] methodParas) {
        try {
            Method method;
            if (methodParas.length == 0)method = clazz.getDeclaredMethod(methodName);
            else method = clazz.getDeclaredMethod(methodName, methodParas);
            HookInvokerRunnable runnable = YuHook.getInvoker(clazz.getName(), methodName);
            val info = new HookInfo(clazz.getName(), methodName, clazz, method, methodParas, runnable);
            runnable.init(info);
            return info;
        } catch (Exception e) {
            throw new RuntimeException(String.format("在创建 HookParas 时遇到问题！位于: %s.%s", clazz.getName(), methodName), e);
        }
    }

    public void saveInfo(String key,Object value){
        if (saves == null) saves = new HashMap<>();
        saves.put(key, value);
    }

    public Object getInfo(String key){
        if (saves == null) return null;
        return saves.get(key);
    }

    public Object delInfo(String key){
        if (saves == null) return null;
        return saves.remove(key);
    }
}

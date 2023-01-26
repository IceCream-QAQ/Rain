package com.IceCreamQAQ.Yu.hook;

import lombok.val;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//public class HookInfo {
//
//    public final String className;
//    public final String methodName;
//
//    public final Class clazz;
//    public final Method method;
//    public final Method sourceMethod;
//    public final Class[] methodParas;
//
//    public final HookInvokerRunnable runnable;
//
//    private Map<String, Object> saves;
//
//    public HookInfo(
//            String className,
//            String methodName,
//            Class<?> clazz,
//            Method method,
//            Method sourceMethod,
//            Class<?>[] methodParas,
//            HookInvokerRunnable runnable
//    ) {
//        this.className = className;
//        this.methodName = methodName;
//        this.clazz = clazz;
//        this.method = method;
//        this.sourceMethod = sourceMethod;
//        this.methodParas = methodParas;
//        this.runnable = runnable;
//    }
//
//    public static HookInfo create(Class clazz, String methodName, String sourceMethodName, Class[] methodParas) {
//        try {
//            Method method;
//            Method sourceMethod;
//            if (methodParas.length == 0) {
//                method = clazz.getDeclaredMethod(methodName);
//                sourceMethod = clazz.getDeclaredMethod(sourceMethodName);
//            } else {
//                method = clazz.getDeclaredMethod(methodName, methodParas);
//                sourceMethod = clazz.getDeclaredMethod(sourceMethodName, methodParas);
//            }
//            HookInvokerRunnable runnable = YuHook.getInvoker(clazz.getName(), methodName);
//            val info = new HookInfo(clazz.getName(), methodName, clazz, method, sourceMethod, methodParas, runnable);
//            runnable.init(info);
//            return info;
//        } catch (Exception e) {
//            throw new RuntimeException(String.format("在创建 HookParas 时遇到问题！位于: %s.%s", clazz.getName(), methodName), e);
//        }
//    }
//
//    public static HookInfo instanceCreate(Class clazz, String methodName, String sourceMethodName, Class[] methodParas) {
//        return null;
//    }
//
//    public void saveInfo(String key, Object value) {
//        if (saves == null) saves = new HashMap<>();
//        saves.put(key, value);
//    }
//
//    public Object getInfo(String key) {
//        if (saves == null) return null;
//        return saves.get(key);
//    }
//
//    public Object delInfo(String key) {
//        if (saves == null) return null;
//        return saves.remove(key);
//    }
//}

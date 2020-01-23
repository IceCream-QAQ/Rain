package com.IceCreamQAQ.Yu.inject;

import lombok.var;

import java.util.concurrent.ConcurrentHashMap;

public class ActionContextInject extends YuQInjectBase{

    public void putInjectObj(String clazz, String name, Object instance) {
        if (name == null) name = "";

        var list = injectObjects.get(clazz);
        if (list == null) {
            list = new ConcurrentHashMap<String, Object>();
            injectObjects.put(clazz, list);
        }
        list.put(name, instance);

    }




}

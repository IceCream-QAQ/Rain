package com.IceCreamQAQ.YuQ.inject;

import com.IceCreamQAQ.YuQ.annotation.Inject;
import lombok.val;
import lombok.var;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YuQInjectBase {

    protected Map<String, Map<String, Object>> injectObjects;

    public YuQInjectBase() {
        injectObjects = new ConcurrentHashMap<>();
    }

    public Object getObj(String clazz, String name) {
        if (name == null) name = "";

        var list = injectObjects.get(clazz);
        if (list == null) return null;
        Object result = list.get("");
        if (result != null) return result;
        return list.get(name);
    }

    public Object getObj(Inject inject, Class clazz) {
        return getObj(clazz.getName(),inject.value().getName(),inject.name());
    }

    public Object getObj(String paraType,String injectType, String name) {
        if (injectType.equals("com.IceCreamQAQ.YuQ.annotation.Inject") || injectType.equals("com.icecreamqaq.yuq.annotation.Inject"))
            injectType = paraType;
        val list = injectObjects.get(injectType);
        if (list == null) {
            return null;
        }
        return list.get(name);
    }

}
